package cash.just.support

import cash.just.support.pages.Topic
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import timber.log.Timber.DebugTree

class CashSupport private constructor(
    private val pageType: PageType,
    private val topic: Topic?,
    private val fromIndex:Boolean = false) {

    init {
        if (Timber.treeCount() == 0) {
            Timber.plant(DebugTree())
        }
    }

    data class Builder(private var pageType: PageType = PageType.INDEX) {
        private var topic: Topic? = null
        private var fromIndex:Boolean = false

        fun detail(topic: Topic, fromIndex:Boolean = false) = apply {
            this.topic = topic
            this.pageType = PageType.DETAIL
            this.fromIndex = fromIndex
        }

        fun build() = CashSupport(pageType, topic, fromIndex)
    }

    fun createDialogFragment(): BottomSheetDialogFragment = run {
        return when(pageType) {
            PageType.INDEX -> {
                IndexDialogFragment()
            }
            PageType.DETAIL -> {
                DetailDialogFragment.newInstance(topic!!, fromIndex)
            }
        }
    }

    enum class PageType {
        INDEX,
        DETAIL
    }
}