package cash.just.atm.fragment

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cash.just.atm.R
import cash.just.atm.base.RequestState
import cash.just.atm.base.showError
import cash.just.atm.viewmodel.StatusViewModel
import cash.just.sdk.model.CashStatus
import cash.just.sdk.model.CodeStatus
import com.square.project.base.singleStateObserve
import kotlinx.android.synthetic.main.fragment_status.*
import kotlinx.android.synthetic.main.request_status_awaiting.*
import kotlinx.android.synthetic.main.request_status_funded.*
import java.util.*

class StatusFragment : Fragment() {
    private val viewModel = StatusViewModel()
    private lateinit var appContext: Context

    enum class ViewState {
        LOADING,
        AWAITING,
        FUNDED
    }

    private lateinit var clipboard: android.content.ClipboardManager
    private lateinit var safeCode: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_request_cash_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appContext = view.context.applicationContext
    }

    private fun refreshCodeStatus(code: String, context: Context) {
        viewModel.checkCashCodeStatus(code)
    }

    private fun changeUiState(state: ViewState) {
        when (state) {
            ViewState.LOADING -> {
                loadingView.visibility = View.VISIBLE
                fundedCard.visibility = View.GONE
                awaitingCard.visibility = View.GONE
            }
            ViewState.AWAITING -> {
                loadingView.visibility = View.GONE
                fundedCard.visibility = View.GONE
                awaitingCard.visibility = View.VISIBLE
            }
            ViewState.FUNDED -> {
                loadingView.visibility = View.GONE
                fundedCard.visibility = View.VISIBLE
                awaitingCard.visibility = View.GONE
            }
        }
    }

    private fun populateAwaitingView(context: Context, cashStatus: CashStatus) {

        changeUiState(ViewState.AWAITING)

        sendAction.setOnClickListener {
            goToSend(cashStatus.btc_amount, cashStatus.address)
        }

        refreshAction.setOnClickListener {
            changeUiState(ViewState.LOADING)
            refreshCodeStatus(safeCode, it.context)
        }

        awaitingAddress.text = cashStatus.address
        awaitingAddress.isSelected = true
        awaitingAddress.setOnClickListener {
            copyToClipboard(context, cashStatus.address)
        }
        awaitingBTCAmount.text = "Amount: ${cashStatus.btc_amount} BTC"
        awaitingBTCAmount.setOnClickListener {
            copyToClipboard(context, cashStatus.btc_amount)
        }

        awaitingLocationAddress.text = "Location: ${cashStatus.description}"

        awaitingLocationAddress.setOnClickListener {
            openMaps(context, cashStatus)
        }

        awaitingUSDAmount.text = "Amount (USD): $${cashStatus.usdAmount}"

        qr_image.setOnClickListener {
            copyToClipboard(context, cashStatus.address)
        }

        //TODO
//        if (!QRUtils.generateQR(activity, uri.toString(), qr_image)) {
//            error("failed to generate qr image for address")
//        }
    }

    private fun goToSend(btc: String, address: String) {
//        val builder = CryptoRequest.Builder()
//        builder.address = address
//        builder.amount = btc.toFloat().toBigDecimal()
//        builder.currencyCode = WalletBitcoinManager.BITCOIN_CURRENCY_CODE
//        val request = builder.build()
//        router.pushController(RouterTransaction.with(SendSheetController(request)))
    }

    private fun populateFundedView(context: Context, cashStatus: CashStatus) {
        changeUiState(ViewState.FUNDED)

        cashCode.text = cashStatus.code!!
        cashCode.setOnClickListener {
            copyToClipboard(context, cashStatus.code!!)
        }
        amountFunded.text = "Amount (USD):  \$${cashStatus.usdAmount}"
        locationFunded.text = "Location: ${cashStatus.description}"
        locationFunded.setOnClickListener {
            openMaps(context, cashStatus)
        }
    }

    private fun copyToClipboard(context: Context, data: String) {
        val clip = ClipData.newPlainText("CNI WALLET", data)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to the clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun openMaps(context: Context, cashStatus: CashStatus) {
        val uri: String = java.lang.String.format(
            Locale.ENGLISH,
            "geo:%f,%f?z=%d&q=%f,%f (%s)",
            cashStatus.latitude,
            cashStatus.longitude,
            15,
            cashStatus.latitude,
            cashStatus.longitude,
            cashStatus.description
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.singleStateObserve(this) { state ->
            when (state) {
                is RequestState.Success -> {
                    when(state.result) {
                        is CashStatus -> {
                            val cashStatus = state.result
                            if (CodeStatus.resolve(cashStatus.status) == CodeStatus.NEW_CODE) {
                                populateAwaitingView(requireContext(), cashStatus)
                            } else if (CodeStatus.resolve(cashStatus.status) == CodeStatus.FUNDED) {
                                populateFundedView(requireContext(), cashStatus)
                            }
                        }
                    }
                }

                is RequestState.Error -> {
                    showError(this, state.throwable)
                }
            }
        }
    }
}
