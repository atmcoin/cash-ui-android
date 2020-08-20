package cash.just.atm.fragment

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cash.just.atm.AtmSharedPreferencesManager
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
import com.square.project.base.singleStateObserve
import kotlinx.android.synthetic.main.fragment_request_cash_code.*
import timber.log.Timber
import java.net.UnknownHostException

class AtmRequestFragment : Fragment() {
    private val viewModel = AtmViewModel()
    private lateinit var appContext: Context

    companion object {
        private const val INITIAL_ZOOM = 15f
        private const val CLICKS_TO_START_ANIMATION = 3
        private const val MAP_FRAGMENT_TAG = "AtmRequestFragment"
    }

    private var coinCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        amount.helperText =
            "Min $${atm.min} max $${atm.max}, multiple of $${atm.bills.toFloat().toInt()} bills"

        getAtmCode.setOnClickListener {
            if (checkFields()) {
                Toast.makeText(view.context, "Complete the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!atm.isValidAmount(getAmount())) {
                val min = atm.min.toFloatOrNull()?.toInt()
                val max = atm.max.toFloatOrNull()?.toInt()
                if (min == null || max == null) {
                    Toast.makeText(
                        view.context, "Amount not valid, " +
                                "it has to be between ${atm.min.toFloatOrNull()?.toInt()} " +
                                "and ${atm.max.toFloatOrNull()?.toInt()}.", Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(view.context, "Amount not valid", Toast.LENGTH_LONG).show()
                }
                return@setOnClickListener
            }

            val amount = getAmount()!!.toFloat().toInt()
            val bills = atm.bills.toFloat().toInt()
            if (amount.rem(bills) != 0) {
                Toast.makeText(view.context, "Amount must be multiple of ${atm.bills}$", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "Token is empty", Toast.LENGTH_SHORT).show()
            confirmAction.hideProgress()
            return
        }

        requireContext().hideKeyboard(code.editText)
        confirmAction.showProgress()
        viewModel.createCashCode(atm, getAmount()!!, getCode()!!)
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
        AlertDialog.Builder(context).setTitle("Withdrawal requested")
            .setMessage("Please send the amount of ${cashStatus.btc_amount} BTC to the ATM")
            .setPositiveButton("Send") { _, _ ->
                getAtmFlow()?.onSend(cashStatus.btc_amount, cashStatus.address)
            }.setNeutralButton("Details") { _, _ ->
                getAtmFlow()?.onDetails(secureCode, cashStatus)
            }.create().show()
    }

    private fun populateVerification(verification: VerificationSent) {
        when(verification.type) {
            VerificationType.EMAIL -> {
                confirmationMessage.text = "We've sent a confirmation code to your email."
                code.helperText =
                    "Check your email for the confirmation code we sent you." +
                            " It may take a couple of minutes."
            }
            VerificationType.PHONE -> {
                confirmationMessage.text = "We've sent a confirmation code to your phone by SMS."
                code.helperText =
                    "Check your SMS inbox for the confirmation code we sent you." +
                            " It may take a couple of minutes."
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

    private fun checkFields(): Boolean {
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
                            Timber.e(state.throwable.message)
                            //it will be probably verification code not found
                            showSnackBar(this, "Invalid code")
                        }
                        is UnknownHostException -> {
                            //it will be probably verification code not found
                            showSnackBar(this, "Unable to confirm the code", R.string.retry) {
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
