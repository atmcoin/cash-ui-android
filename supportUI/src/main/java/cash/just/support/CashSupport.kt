package cash.just.support

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import timber.log.Timber.DebugTree

class CashSupport private constructor(
    private val pageType: PageType,
    private val supportPage: BaseSupportPage?,
    private val fromIndex:Boolean = false) {

    init {
        if (Timber.treeCount() == 0) {
            Timber.plant(DebugTree())
        }
    }

    data class Builder(private var pageType: PageType = PageType.INDEX) {
        private var supportPage: BaseSupportPage? = null
        private var fromIndex:Boolean = false

        fun detail(supportPage: BaseSupportPage, fromIndex:Boolean = false) = apply {
            this.pageType = PageType.DETAIL
            this.supportPage = supportPage
            this.fromIndex = fromIndex
        }

        fun build() = CashSupport(pageType, supportPage, fromIndex)
    }

    fun createDialogFragment(): BottomSheetDialogFragment = run {
        return when(pageType) {
            PageType.INDEX -> {
                IndexDialogFragment()
            }
            PageType.DETAIL -> {
                DetailDialogFragment.newInstance(supportPage!!, fromIndex)
            }
        }
    }

    enum class PageType {
        INDEX,
        DETAIL
    }
}