package cash.just.atm

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import cash.just.atm.AtmMapHelper
import cash.just.sdk.CashSDK
import cash.just.sdk.model.AtmMachine
import cash.just.sdk.model.CashCodeResponse
import cash.just.sdk.model.CashCodeStatusResponse
import cash.just.sdk.model.CashStatus
import cash.just.sdk.model.SendVerificationCodeResponse
import cash.just.sdk.model.isValidAmount
import cash.just.sdk.model.parseError
import com.bluelinelabs.conductor.RouterTransaction
import com.breadwallet.R
import com.breadwallet.legacy.presenter.entities.CryptoRequest
import com.breadwallet.legacy.wallet.wallets.bitcoin.WalletBitcoinManager
import com.breadwallet.tools.animation.BRDialog
import com.breadwallet.ui.BaseController
import com.breadwallet.ui.atm.model.RetryableCashStatus
import com.breadwallet.ui.atm.model.AtmMarker
import com.breadwallet.ui.platform.PlatformConfirmTransactionController
import com.breadwallet.ui.send.SendSheetController
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.platform.PlatformTransactionBus
import kotlinx.android.synthetic.main.fragment_request_cash_code.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("TooManyFunctions")
class RequestCashCodeController(
    args: Bundle
) : BaseController(args) {

    constructor(atm: AtmMachine) : this(
        bundleOf(atmMachine to atm)
    )

    companion object {
        private const val INITIAL_ZOOM = 15f
        private const val HTTP_OK_CODE = 200
        private const val CLICKS_TO_START_ANIMATION = 3
        private const val atmMachine = "RequestCashCodeController.Atm"
        private const val MAP_FRAGMENT_TAG = "MAP_FRAGMENT_TAG"
    }

    override val layoutId = R.layout.fragment_request_cash_code

    private enum class VerificationState {
        PHONE,
        EMAIL
    }

    private var currentVerificationMode: VerificationState = VerificationState.PHONE
    private var coinCount = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(view: View) {
        super.onCreateView(view)

        val atm: AtmMachine = arg(atmMachine)

        verificationGroup.visibility = View.VISIBLE
        confirmGroup.visibility = View.GONE

        // noPhoneButton.setOnClickListener {
        //     toggleVerification()
        // }

        handlePlatformMessages().launchIn(viewCreatedScope)

        prepareMap(view.context, atm)

        atmTitle.text = atm.addressDesc
        amount.helperText =
            "Min $${atm.min} max $${atm.max}, multiple of $${atm.bills.toFloat().toInt()} bills"

        getAtmCode.setOnClickListener {

            if (!CashSDK.isSessionCreated()) {
                Toast.makeText(view.context, "Invalid session", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
                Toast.makeText(
                    view.context,
                    "Amount must be multiple of ${atm.bills}$",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            requestVerificationCode(view.context)
        }

        confirmAction.setOnClickListener {

            if (!CashSDK.isSessionCreated()) {
                Toast.makeText(view.context, "invalid session", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (getCode().isNullOrEmpty()) {
                Toast.makeText(view.context, "Token is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            hideKeyboard(view.context, code.editText!!)
            createCashCode(view.context, atm)
        }

        dropView.setDrawables(R.drawable.bitcoin, R.drawable.bitcoin)
    }

    private fun playCoinSound() {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.smw_coin)
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

    private fun requestVerificationCode(context: Context) {
        CashSDK.sendVerificationCode(
            getName()!!,
            getSurname()!!,
            getPhone(),
            getEmail()
        ).enqueue(object : Callback<SendVerificationCodeResponse> {
            override fun onResponse(
                call: Call<SendVerificationCodeResponse>,
                response: Response<SendVerificationCodeResponse>
            ) {
                if (response.code() == HTTP_OK_CODE) {
                    Toast.makeText(
                        context,
                        response.body()!!.data.items[0].result,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (getEmail() != null && getEmail()!!.isNotEmpty()) {
                        confirmationMessage.text = "We've sent a confirmation code to your email."
                        code.helperText =
                            "Check your email for the confirmation code we sent you." +
                                " It may take a couple of minutes."
                    } else {
                        confirmationMessage.text =
                            "We've sent a confirmation code to your phone by SMS."
                        code.helperText =
                            "Check your SMS inbox for the confirmation code we sent you." +
                                " It may take a couple of minutes."
                    }
                    verificationGroup.visibility = View.GONE
                    confirmGroup.visibility = View.VISIBLE
                } else {
                    Toast.makeText(context, "error" + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SendVerificationCodeResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hideKeyboard(context: Context, editText: EditText) {
        val imm: InputMethodManager? =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun createCashCode(context: Context, atm: AtmMachine) {

        CashSDK.createCashCode(atm.atmId, getAmount()!!, getCode()!!)
            .enqueue(object : Callback<CashCodeResponse> {
                override fun onResponse(call: Call<CashCodeResponse>, response: Response<CashCodeResponse>) {
                    if (response.code() == HTTP_OK_CODE) {
                        val secureCode = response.body()!!.data.items[0].secureCode
                        proceedWithCashCode(context, secureCode)
                    } else {
                        val errorBody = response.errorBody()
                        errorBody?.let {
                            it.parseError().error.server_message.let { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                return
                            }
                        }
                        Toast.makeText(context, "error " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CashCodeResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun proceedWithCashCode(context: Context, secureCode: String) {
        AtmSharedPreferencesManager.setWithdrawalRequest(context, secureCode)

        CashSDK.checkCashCodeStatus(secureCode).enqueue(object : Callback<CashCodeStatusResponse> {
            override fun onResponse(call: Call<CashCodeStatusResponse>, response: Response<CashCodeStatusResponse>) {
                val cashStatus = response.body()!!.data!!.items[0]
                showDialog(context, secureCode, cashStatus)
            }

            override fun onFailure(call: Call<CashCodeStatusResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDialog(context: Context, secureCode: String, cashStatus: CashStatus) {

        BRDialog.cancellableShowCustomDialog(
            context, "Withdrawal requested",
            "Please send the amount of ${cashStatus.btc_amount} BTC to the ATM",
            "Send", "Details", { dialog ->
                dialog.dismissWithAnimation()
                goToSend(cashStatus.btc_amount, cashStatus.address)
            },
            { dialog ->
                dialog.dismissWithAnimation()
                goToDetails(secureCode, cashStatus)
            }, null, false
        )
    }

    private fun prepareMap(context: Context, atm: AtmMachine) {
        val fragment = createAndHideMap(context)
        fragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap?) {
                googleMap ?: return

                googleMap.uiSettings.isMapToolbarEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false
                googleMap.uiSettings.isScrollGesturesEnabled = false
                googleMap.uiSettings.isZoomGesturesEnabled = false
                googleMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false
                showMap(context)
                googleMap.setOnMapLoadedCallback(OnMapLoadedCallback {
                    addMarkerAndMoveCamera(context, googleMap, atm)
                    coinCount = 0
                    dropView.stopAnimation()
                })

                googleMap.setOnInfoWindowClickListener {
                    playCoinSound()
                }
            }
        })
    }

    private fun goToDetails(secureCode: String, status: CashStatus) {
        router.replaceTopController(
            RouterTransaction.with(
                CashOutStatusController(
                    RetryableCashStatus(secureCode, status)
                )
            )
        )
    }

    private fun goToSend(btc: String, address: String) {
        val builder = CryptoRequest.Builder()
        builder.address = address
        builder.amount = btc.toFloat().toBigDecimal()
        builder.currencyCode = WalletBitcoinManager.BITCOIN_CURRENCY_CODE
        val request = builder.build()
        router.replaceTopController(
            RouterTransaction.with(
                SendSheetController(
                    request //make it default
                )
            )
        )
    }

    private fun showMap(context: Context) {
        val fragmentManager = AtmMapHelper.getActivityFromContext(context)!!.supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG)
        fragment?.let {
            fragmentManager.beginTransaction()
                .show(fragment)
                .commit()
        }
    }

    private fun createAndHideMap(context: Context): SupportMapFragment {
        val fragment = AtmMapHelper.addMapFragment(context, R.id.smallMapFragment, MAP_FRAGMENT_TAG)
        val fragmentManager = AtmMapHelper.getActivityFromContext(context)!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .hide(fragment)
            .commit()
        return fragment
    }

    private fun addMarkerAndMoveCamera(context: Context, googleMap: GoogleMap, atm: AtmMachine) {
        val markerOpt = AtmMarker.getMarker(context, atm)

        val marker = googleMap.addMarker(markerOpt)
        marker.tag = atm

        val cameraPosition: CameraPosition = CameraPosition.Builder()
            .target(markerOpt.position)
            .zoom(INITIAL_ZOOM)
            .build()

        val cameraUpdate: CameraUpdate = CameraUpdateFactory
            .newCameraPosition(cameraPosition)
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

    private fun toggleVerification() {
        if (currentVerificationMode == VerificationState.PHONE) {
            phoneNumber.visibility = View.GONE
            email.visibility = View.VISIBLE
            // noPhoneButton.text = "Phone Number"
            currentVerificationMode = VerificationState.EMAIL
        } else {
            phoneNumber.visibility = View.VISIBLE
            email.visibility = View.GONE
            // noPhoneButton.text = "No Phone?"
            currentVerificationMode = VerificationState.PHONE
        }
    }

    private fun handlePlatformMessages() = PlatformTransactionBus.requests().onEach {
        withContext(Dispatchers.Main) {
            val transaction = RouterTransaction.with(PlatformConfirmTransactionController(it))
            router.pushController(transaction)
        }
    }
}
