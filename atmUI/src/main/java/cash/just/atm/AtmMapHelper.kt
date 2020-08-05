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
                .commit()
            fragmentManager.executePendingTransactions()
        }

        fragmentManager
            .beginTransaction()
            .add(containerViewId, fragment, tag)
            .commit()

        return fragment as SupportMapFragment
    }
}