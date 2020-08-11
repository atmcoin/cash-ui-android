package cash.just.atm.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cash.just.atm.R
import cash.just.atm.base.RequestState
import cash.just.atm.base.showError
import cash.just.atm.extension.formatTo
import cash.just.atm.extension.toDate
import cash.just.atm.model.RetryableCashStatus
import cash.just.atm.viewmodel.CashStatusResult
import cash.just.atm.viewmodel.StatusViewModel
import cash.just.sdk.model.CashStatus
import cash.just.sdk.model.CodeStatus
import com.square.project.base.singleStateObserve
import kotlinx.android.synthetic.main.fragment_request_list.*

class StatusListFragment : Fragment() {
    private val viewModel = StatusViewModel()
    private lateinit var appContext: Context
    private lateinit var state:ViewState

    companion object {
        const val SEVER_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DISPLAY_TIME_FORMAT = "dd MMM, hh:mm"
        const val REFRESH_UI_DELAY_MS = 400L
    }

    enum class ViewState {
        LOADING,
        EMPTY,
        LIST
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_request_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appContext = view.context.applicationContext

        setState(ViewState.LOADING)
        proceed()
    }

    private fun setState(viewState:ViewState){
        when(viewState) {
            ViewState.LOADING -> {
                loadingView.visibility = View.VISIBLE
                emptyStateGroup.visibility = View.GONE
            }
            ViewState.EMPTY -> {
                loadingView.visibility = View.GONE
                emptyStateGroup.visibility = View.VISIBLE
            }
            ViewState.LIST -> {
                loadingView.visibility = View.GONE
                emptyStateGroup.visibility = View.GONE
            }
        }
        state = viewState
    }
    private fun proceed() {
        refreshListAction.visibility = View.VISIBLE

        refreshListAction.setOnClickListener {
            setState(ViewState.LOADING)
            requestGroup.removeAllViews()
            refreshListAction.postDelayed({
                if (refreshListAction != null) {
                    viewModel.getCashCodes(requireContext())
                }
            }, REFRESH_UI_DELAY_MS)
        }

        viewModel.getCashCodes(requireContext())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.singleStateObserve(this) { state ->
            when (state) {
                is RequestState.Success -> {
                   val list = state.result as ArrayList<CashStatusResult>
                   if (list.isEmpty()) {
                       setState(ViewState.EMPTY)
                   } else {
                       createStatusRows(list)
                       setState(ViewState.LIST)
                   }
                }

                is RequestState.Error -> {
                    showError(this, state.throwable)
                }
            }
        }
    }

    private fun createStatusRows(list:ArrayList<CashStatusResult>) {
        list.sortBy {
            it.status.expiration
        }

        list.forEach {
            if (requestGroup != null) {
                val view = View.inflate(context, R.layout.item_list_cash_out_request, null)
                populateCashCodeStatus(view, it.cashCode, it.status)
                requestGroup.addView(view)
            }
        }
    }

    private fun populateCashCodeStatus(view:View, secureCode:String, cashStatus: CashStatus) {
        view.findViewById<TextView>(R.id.date).text =
            cashStatus.expiration
                .toDate(SEVER_TIME_FORMAT)
                .formatTo(DISPLAY_TIME_FORMAT)
        view.findViewById<TextView>(R.id.addressLocation).text = cashStatus.description
        val status = CodeStatus.resolve(cashStatus.status)
        val stateView = view.findViewById<TextView>(R.id.stateMessage)
        when (status) {
            CodeStatus.NEW_CODE -> {
                stateView.text = "Awaiting funds"
                setClickListener(stateView, secureCode, cashStatus)
                val drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_eye)
                stateView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            }
            CodeStatus.FUNDED -> {
                stateView.text = "Funded"
                setClickListener(stateView, secureCode, cashStatus)
                val drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_cash)
                stateView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            }
            else -> {
                val state = CodeStatus.resolve(cashStatus.status).toString().toLowerCase()
                val capitalizeFirst = state.substring(0, 1).toUpperCase() + state.substring(1)
                stateView.text = capitalizeFirst
            }
        }
    }

    private fun setClickListener(textView: TextView, secureCode: String, cashStatus : CashStatus){
        textView.setOnClickListener {
            val retryableCashStatus =
                RetryableCashStatus(
                    secureCode,
                    cashStatus
                )
//            router.pushController(RouterTransaction.with(CashOutStatusController(retryableCashStatus)))
            findNavController().navigate(StatusListFragmentDirections.listToStatus(secureCode))
        }
    }
}
