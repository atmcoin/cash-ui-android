package cash.just.atm

import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.SupportMapFragment

object AtmMapHelper {
    fun addMapFragment(fragmentManager: FragmentManager, containerViewId:Int, tag:String) : SupportMapFragment {
        var fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) fragment = SupportMapFragment()

        if (fragment.isAdded) {
            fragmentManager
                .beginTransaction()
                .remove(fragment)
                .commitNow()
        }

        fragmentManager
            .beginTransaction()
            .add(containerViewId, fragment, tag)
            .commitNow()

        fragmentManager.executePendingTransactions()

        return fragment as SupportMapFragment
    }
}