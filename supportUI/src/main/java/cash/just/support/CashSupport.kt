package cash.just.support

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CashSupport private constructor(
    private val pageType: PageType,
    private val supportPage: SupportPage?) {

    data class Builder(private var pageType: PageType = PageType.INDEX) {
        private var supportPage: SupportPage? = null

        fun detail(supportPage: SupportPage) = apply {
            this.pageType = PageType.DETAIL
            this.supportPage = supportPage
        }

        fun build() = CashSupport(pageType, supportPage)
    }

    fun createDialogFragment(): BottomSheetDialogFragment = run {
        return when(pageType) {
            PageType.INDEX -> {
                IndexDialogFragment()
            }
            PageType.DETAIL -> {
                DetailDialogFragment.newInstance(supportPage!!)
            }
        }
    }

    enum class PageType {
        INDEX,
        DETAIL
    }
}