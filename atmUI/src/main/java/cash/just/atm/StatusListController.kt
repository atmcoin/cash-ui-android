//package cash.just.atm
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.os.Bundle
//import android.view.View
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import cash.just.sdk.Cash
//import cash.just.sdk.CashSDK
//import cash.just.sdk.model.CashCodeStatusResponse
//import cash.just.sdk.model.CashStatus
//import cash.just.sdk.model.CodeStatus
//import com.bluelinelabs.conductor.RouterTransaction
//import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
//import com.breadwallet.R
//import com.breadwallet.ui.BaseController
//import com.breadwallet.ui.atm.model.RetryableCashStatus
//import com.breadwallet.ui.changehandlers.BottomSheetChangeHandler
//import com.breadwallet.ui.formatTo
//import com.breadwallet.ui.platform.PlatformConfirmTransactionController
//import com.breadwallet.ui.toDate
//import com.platform.PlatformTransactionBus
//import kotlinx.android.synthetic.main.fragment_request_list.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.launchIn
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.withContext
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class StatusListController(args: Bundle) : BaseController(args) {
//
//    override val layoutId = R.layout.fragment_request_list
//    var statusList = ArrayList<CashStatus>()
//    var size = 0
//
//    companion object {
//        const val SEVER_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
//        const val DISPLAY_TIME_FORMAT = "dd MMM, hh:mm"
//        const val HTTP_OK = 200
//        const val REFRESH_UI_DELAY_MS = 400L
//    }
//
//    init {
//        overridePushHandler(BottomSheetChangeHandler())
//        overridePopHandler(BottomSheetChangeHandler())
//    }
//
//    @SuppressLint("SetJavaScriptEnabled")
//    override fun onCreateView(view: View) {
//        super.onCreateView(view)
//        prepareEmptyView()
//
//        size = 0
//        loadingView.visibility = View.VISIBLE
//        text_no_request.visibility = View.GONE
//
//        val context = view.context
//        if (!CashSDK.isSessionCreated()) {
//            CashSDK.createSession(BitcoinServer.getServer(), object: Cash.SessionCallback {
//                override fun onSessionCreated(sessionKey: String) {
//                    proceed(context)
//                }
//
//                override fun onError(errorMessage: String?) {
//                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            })
//        } else {
//            proceed(context)
//        }
//
//        handlePlatformMessages().launchIn(viewCreatedScope)
//    }
//
//    private fun prepareEmptyView() {
//        emptyStateGroup.visibility = View.GONE
//        requestAction.setOnClickListener {
//            router.pushController(
//                RouterTransaction.with(MapController(Bundle.EMPTY))
//                    .pushChangeHandler(HorizontalChangeHandler())
//                    .popChangeHandler(HorizontalChangeHandler())
//            )
//        }
//    }
//
//    private fun proceed(context: Context) {
//        refreshListAction.visibility = View.VISIBLE
//
//        refreshListAction.setOnClickListener {
//            loadingView.visibility = View.VISIBLE
//            requestGroup.removeAllViews()
//            refreshListAction.postDelayed(Runnable {
//                if (refreshListAction != null) {
//                    refresh(context)
//                }
//            }, REFRESH_UI_DELAY_MS)
//        }
//
//        refresh(context)
//    }
//
//    private fun refresh(context:Context) {
//        loadingView.visibility = View.VISIBLE
//        statusList = ArrayList()
//        val requests = AtmSharedPreferencesManager.getWithdrawalRequests(context)
//        requests?.let {
//            size = it.size
//            it.forEach { string ->
//                loadRequest(context, string)
//            }
//        }
//
//        if (requests == null || requests.isEmpty()) {
//            emptyStateGroup.visibility = View.VISIBLE
//            refreshListAction.visibility = View.GONE
//            text_no_request.visibility = View.VISIBLE
//            loadingView.visibility = View.GONE
//        }
//    }
//
//    private fun loadRequest(context: Context, secureCode:String) {
//        CashSDK.checkCashCodeStatus(secureCode).enqueue(object: Callback<CashCodeStatusResponse> {
//            override fun onResponse(call: Call<CashCodeStatusResponse>, response: Response<CashCodeStatusResponse>) {
//                if (loadingView != null) {
//                    loadingView.visibility = View.GONE
//
//                    if (response.isSuccessful && response.code() == HTTP_OK) {
//                        statusList.add(response.body()?.data!!.items[0])
//                    }
//
//                    if (statusList.size == size) {
//                        createStatusRows(secureCode, context)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<CashCodeStatusResponse>, t: Throwable) {
//                Toast.makeText(applicationContext, "Failed to load $secureCode status", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun createStatusRows(secureCode: String, context:Context){
//        statusList.sortBy {
//            it.expiration
//        }
//
//        statusList.forEach { status ->
//            if (requestGroup != null) {
//                val view = View.inflate(context, R.layout.item_list_cash_out_request, null)
//                populateCashCodeStatus(view, secureCode, status)
//                requestGroup.addView(view)
//            }
//        }
//    }
//
//    private fun populateCashCodeStatus(view:View, secureCode:String, cashStatus: CashStatus) {
//        view.findViewById<TextView>(R.id.date).text =
//            cashStatus.expiration.toDate(SEVER_TIME_FORMAT).formatTo(DISPLAY_TIME_FORMAT)
//        view.findViewById<TextView>(R.id.addressLocation).text = cashStatus.description
//        val status = CodeStatus.resolve(cashStatus.status)
//        val stateView = view.findViewById<TextView>(R.id.stateMessage)
//        when (status) {
//            CodeStatus.NEW_CODE -> {
//                stateView.text = "Awaiting funds"
//                setClickListener(stateView, secureCode, cashStatus)
//                val drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_eye)
//                stateView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
//            }
//            CodeStatus.FUNDED -> {
//                stateView.text = "Funded"
//                setClickListener(stateView, secureCode, cashStatus)
//                val drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_cash)
//                stateView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
//            }
//            else -> {
//                val state = CodeStatus.resolve(cashStatus.status).toString().toLowerCase()
//                val capitalizeFirst = state.substring(0, 1).toUpperCase() + state.substring(1)
//                stateView.text = capitalizeFirst
//            }
//        }
//    }
//
//    private fun setClickListener(textView: TextView, secureCode: String, cashStatus : CashStatus){
//        textView.setOnClickListener {
//            val retryableCashStatus =
//                RetryableCashStatus(
//                    secureCode,
//                    cashStatus
//                )
//            router.pushController(RouterTransaction.with(CashOutStatusController(retryableCashStatus)))
//        }
//    }
//
//    private fun handlePlatformMessages() = PlatformTransactionBus.requests().onEach {
//        withContext(Dispatchers.Main) {
//            val transaction = RouterTransaction.with(PlatformConfirmTransactionController(it))
//            router.pushController(transaction)
//        }
//    }
//}
