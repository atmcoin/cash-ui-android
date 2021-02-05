package cash.just.atm.fragment

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cash.just.atm.AtmSharedPreferencesManager
import cash.just.atm.PhoneValidator
import cash.just.atm.R
import cash.just.atm.base.AtmFlow
import cash.just.atm.base.RequestState
import cash.just.atm.base.showError
import cash.just.atm.base.showSnackBar
import cash.just.atm.extension.hideKeyboard
import cash.just.atm.model.AtmMarker
import cash.just.atm.model.VerificationType
import cash.just.atm.viewmodel.AtmViewModel
import cash.just.atm.viewmodel.VerificationSent
import cash.just.sdk.model.AtmMachine
import cash.just.sdk.model.CashCode
import cash.just.sdk.model.CashStatus
import cash.just.sdk.model.isValidAmount
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.square.project.base.singleStateObserve
import kotlinx.android.synthetic.main.fragment_request_cash_code.*
import timber.log.Timber
import java.net.UnknownHostException
import java.util.*

class AtmRequestFragment : Fragment() {
    private val viewModel = AtmViewModel()
    private lateinit var appContext: Context
    private lateinit var functions: FirebaseFunctions

    companion object {
        private const val INITIAL_ZOOM = 15f
        private const val CLICKS_TO_START_ANIMATION = 3
        private const val MAP_FRAGMENT_TAG = "AtmRequestFragment"
    }

    private var coinCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        functions = Firebase.functions
        return inflater.inflate(R.layout.fragment_request_cash_code, container, false)
    }

    private fun getAtmArgs():AtmMachine {
        return AtmRequestFragmentArgs.fromBundle(requireArguments()).atmMachine
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appContext = view.context.applicationContext

        verificationGroup.visibility = View.VISIBLE
        confirmGroup.visibility = View.GONE
        val atm = getAtmArgs()

        prepareMap(view.context, atm)

        preFillSavedData()
        atmTitle.text = atm.addressDesc
        amount.helperText = getString(R.string.request_cash_out_amount_validation, atm.min, atm.max, atm.bills.toFloat().toInt())

        getAtmCode.setOnClickListener {
            if (!PhoneValidator(getPhone()).isValid()) {
                Toast.makeText(view.context, getString(R.string.request_cash_invalid_phone_number), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (areFieldsInvalid()) {
                Toast.makeText(view.context, getString(R.string.request_cash_out_complete_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!atm.isValidAmount(getAmount())) {
                val min = atm.min.toFloatOrNull()?.toInt()
                val max = atm.max.toFloatOrNull()?.toInt()
                if (min == null || max == null) {
                    Toast.makeText(
                        view.context, getString(R.string.request_cash_out_amount_not_valid_min_max, min, max), Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(view.context, getString(R.string.request_cash_out_amount_not_valid), Toast.LENGTH_LONG).show()
                }
                return@setOnClickListener
            }

            val amount = getAmount()!!.toFloat().toInt()
            val bills = atm.bills.toFloat().toInt()
            if (amount.rem(bills) != 0) {
                Toast.makeText(view.context, getString(R.string.request_cash_out_amount_multiple, bills), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            getAtmCode.showProgress()
            viewModel.sendVerificationCode(requireContext(), getName()!!, getSurname()!!, getPhone(), getEmail())
        }

        confirmAction.setOnClickListener {
            confirmCode()
        }

        dropView.setDrawables(R.drawable.bitcoin, R.drawable.bitcoin)
    }

    private fun preFillSavedData() {
        val context = requireContext()
        AtmSharedPreferencesManager.getFirstName(context)?.let {
           firstName.editText?.setText(it)
        }
        AtmSharedPreferencesManager.getLastName(context)?.let {
            lastName.editText?.setText(it)
        }
        AtmSharedPreferencesManager.getPhone(context)?.let {
            phoneNumber.editText?.setText(it)
        }
        AtmSharedPreferencesManager.getEmail(context)?.let {
            email.editText?.setText(it)
        }
    }

    private fun confirmCode() {
        val atm = getAtmArgs()

        if (getCode().isNullOrEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.request_cash_out_empty_code), Toast.LENGTH_SHORT).show()
            confirmAction.hideProgress()
            return
        }

        requireContext().hideKeyboard(code.editText)
        confirmAction.showProgress()

        //update phone number in firestore with FCM_TOKEN
        updatePhoneNumber()
        viewModel.createCashCode(atm, getAmount()!!, getCode()!!)
    }

    private fun updatePhoneNumber() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.tag(ContentValues.TAG).w(task.exception, "Fetching FCM registration token failed")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result

            registerToken(token)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val e = task.exception
                        if (e is FirebaseFunctionsException) {
                            val code = e.code
                            val details = e.details
                            Timber.tag(ContentValues.TAG).d("${code}:  $details")
                        }
                    } else {
                        Timber.tag(ContentValues.TAG).d("Task successful")
                    }
                }

            Timber.tag(ContentValues.TAG).d(token)
        })
    }

    private fun registerToken(token: String?): Task<String> {
        val updatedAt = Calendar.getInstance().timeInMillis
        val data = hashMapOf(
            "fcmToken" to token,
            "phone" to getPhone(),
            "deviceModel" to Build.MODEL,
            "updatedAt" to updatedAt
        )
        Timber.tag(ContentValues.TAG).d(getPhone())
        return functions
            .getHttpsCallable("registerToken")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data
                result.toString()
            }.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.tag(ContentValues.TAG).d("task unsuccessful: ${task.exception}")
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                        Timber.tag(ContentValues.TAG).d("${code}: $details")
                    }
                } else {
                    Timber.tag(ContentValues.TAG).d("Task successful")
                }
            })
    }

    private fun getAtmFlow(): AtmFlow? {
        activity?.let {
            if (it is AtmFlow) {
                return it
            }
            throw IllegalStateException("Parent activity must implement " + AtmFlow::class.java.name)
        }?:run {
            return null
        }
    }

    private fun showDialog(context: Context, secureCode: String, cashStatus: CashStatus) {
        AlertDialog.Builder(context).setTitle(R.string.request_cash_out_dialog_title)
            .setMessage(getString(R.string.request_cash_out_dialog_message, cashStatus.btc_amount))
            .setPositiveButton(R.string.request_cash_out_dialog_send_action) { _, _ ->
                getAtmFlow()?.onSend(cashStatus.btc_amount, cashStatus.address)
            }.setNeutralButton(R.string.request_cash_out_dialog_details_action) { _, _ ->
                getAtmFlow()?.onDetails(secureCode, cashStatus)
            }.create().show()
    }

    private fun populateVerification(verification: VerificationSent) {
        when(verification.type) {
            VerificationType.EMAIL -> {
                confirmationMessage.text = getString(R.string.request_cash_out_email_confirmation)
                code.helperText = getString(R.string.request_cash_out_email_confirmation_message)
            }
            VerificationType.PHONE -> {
                confirmationMessage.text = getString(R.string.request_cash_out_phone_confirmation)
                code.helperText = getString(R.string.request_cash_out_phone_confirmation_message)
            }
        }
        verificationGroup.visibility = View.GONE
        confirmGroup.visibility = View.VISIBLE
    }

    private fun prepareMap(context: Context, atm: AtmMachine) {
        val fragment = addMap()
        fragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap?) {
                fragment.view?.let {
                    it.visibility = View.GONE
                }
                googleMap ?: return

                googleMap.uiSettings.isMapToolbarEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false
                googleMap.uiSettings.isScrollGesturesEnabled = false
                googleMap.uiSettings.isZoomGesturesEnabled = false
                googleMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false
                googleMap.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {
                    addMarkerAndMoveCamera(context, googleMap, atm)
                    coinCount = 0
                    dropView.stopAnimation()

                    fragment.view?.let {
                        it.visibility = View.VISIBLE
                    }
                })

                googleMap.setOnInfoWindowClickListener {
                    playCoinSound()
                }
            }
        })
    }

    private fun playCoinSound() {
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.smw_coin)
        mediaPlayer.start()

        coinCount++
        mediaPlayer.setOnCompletionListener { mp ->
            mp?.let {
                it.reset()
                it.release()
            }
        }

        if (coinCount == CLICKS_TO_START_ANIMATION) {
            dropView.startAnimation()
        }
    }

    private fun addMap():SupportMapFragment {
        val fragmentManager = childFragmentManager
        val transition = fragmentManager.beginTransaction()
        var fragment = fragmentManager.findFragmentById(R.id.smallMapFragment)
        if (fragment == null) {
            fragment = SupportMapFragment()
            transition.add(R.id.smallMapFragment, fragment, MAP_FRAGMENT_TAG)
        }
        transition.commit()
        fragmentManager.executePendingTransactions()
        return fragment as SupportMapFragment
    }

    private fun addMarkerAndMoveCamera(context: Context, googleMap: GoogleMap, atm: AtmMachine) {
        val markerOpt = AtmMarker.getMarker(context, atm)

        val marker = googleMap.addMarker(markerOpt)
        marker.tag = atm

        val cameraPosition: CameraPosition = CameraPosition.Builder()
            .target(markerOpt.position)
            .zoom(INITIAL_ZOOM)
            .build()

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        googleMap.moveCamera(cameraUpdate)
    }

    private fun areFieldsInvalid(): Boolean {
        return getAmount().isNullOrEmpty() ||
                getName().isNullOrEmpty() || getSurname().isNullOrEmpty() ||
                (getEmail().isNullOrEmpty() && getPhone().isNullOrEmpty())
    }

    private fun getAmount(): String? {
        return amount.editText?.text.toString()
    }

    private fun getCode(): String? {
        return code.editText?.text.toString()
    }

    private fun getName(): String? {
        return firstName.editText?.text.toString()
    }

    private fun getSurname(): String? {
        return lastName.editText?.text.toString()
    }

    private fun getPhone(): String? {
        return phoneNumber.editText?.text.toString()
    }

    private fun getEmail(): String? {
        return email.editText?.text.toString()
    }

    private fun clearLoadingButtons() {
        getAtmCode.hideProgress()
        confirmAction.hideProgress()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.singleStateObserve(this) { state ->
            when (state) {
                is RequestState.Success -> {
                    when (state.result) {
                        is VerificationSent -> {
                            populateVerification(state.result)
                        }
                        is AtmViewModel.CashCodeStatusResult -> {
                            showDialog(requireContext(), state.result.secureCode, state.result.cashCodeStatus)
                        }
                        is CashCode -> {
                            viewModel.checkCashCodeStatus(requireContext(), state.result.secureCode)
                        }
                    }
                }

                is RequestState.Error -> {
                    clearLoadingButtons()
                    when(state.throwable) {
                        is java.lang.IllegalStateException -> {
                            Timber.e(state.throwable)
                            //it will be probably verification code not found
                            showSnackBar(this, getString(R.string.request_cash_out_invalid_code))
                        }
                        is UnknownHostException -> {
                            //it will be probably verification code not found
                            showSnackBar(this, getString(R.string.request_cash_out_unable_to_confirm), R.string.retry) {
                                confirmCode()
                            }
                        }
                        else -> {
                            showError(this, state.throwable)
                        }
                    }
                }
            }
        }
    }
}
