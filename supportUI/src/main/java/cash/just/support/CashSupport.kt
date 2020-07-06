package cash.just.support

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import timber.log.Timber.DebugTree


class CashSupport private constructor(
    private val pageType: PageType,
    private val supportPage: BaseSupportPage?) {

    init {
        if (Timber.treeCount() == 0) {
            Timber.plant(DebugTree())
        }
    }

    data class Builder(private var pageType: PageType = PageType.INDEX) {
        private var supportPage: BaseSupportPage? = null

        fun detail(supportPage: BaseSupportPage) = apply {
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