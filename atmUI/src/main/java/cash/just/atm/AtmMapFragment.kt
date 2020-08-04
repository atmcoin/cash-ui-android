package cash.just.atm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cash.just.atm.model.AtmClusterRenderer
import cash.just.atm.model.AtmMarker
import cash.just.atm.model.AtmMarkerInfo
import cash.just.atm.model.ClusteredAtm
import cash.just.sdk.Cash
import cash.just.sdk.CashSDK
import cash.just.sdk.model.AtmMachine
import cash.just.support.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import cash.just.atm.base.RequestState
import com.square.project.base.singleStateObserve
import kotlinx.android.synthetic.main.fragment_map.*
import okhttp3.internal.filterList
import timber.log.Timber

enum class ATMViewMode {
    MAP,
    LIST
}

enum class ATMMode {
    ALL,
    REDEMPTION
}

class AtmMapFragment : Fragment() {
    private val viewModel = AtmViewModel()

    private var viewMode:ATMViewMode = ATMViewMode.MAP
    private var atmMode:ATMMode = ATMMode.ALL

    private var map: GoogleMap? = null
    private var atmList: List<AtmMachine> = ArrayList()

    private var texas = LatLng(31.000000, -100.000000)
    private var initialZoom = 5f
    private lateinit var appContext:Context
    private val enableMapClusters = false
    private val clustersThreshold = 100
    private var isAllAtms = true
    private val fastAdapter = FastItemAdapter<AtmItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appContext = view.context.applicationContext
        prepareMap(appContext)
        buttonRedeemOnly.isEnabled = false
        loadingView.visibility = View.VISIBLE
        atmRecyclerList.visibility = View.GONE

        viewModel.getAtms()

        fastAdapter.itemFilter.filterPredicate = { item: AtmItem, constraint: CharSequence? ->
            item.atmMachine.addressDesc.contains(constraint.toString(), ignoreCase = true)
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                switchToMode(ATMViewMode.LIST)
                fastAdapter.filter(newText)
                return false
            }
        })

        atmRecyclerList.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )

        atmRecyclerList.adapter = fastAdapter

        fastAdapter.onClickListener = { _, _, item, _ ->
            // Handle click here
            if (item.atmMachine.redemption == 1) {
                moveToVerification(item.atmMachine)
            } else {
                Toast.makeText(activity, "This ATM is purchase only.", Toast.LENGTH_SHORT).show()
            }
            false
        }

        listMapButton.setOnClickListener {
            viewMode = when (viewMode) {
                ATMViewMode.MAP -> {
                    switchToMode(ATMViewMode.MAP)
                    ATMViewMode.LIST
                }
                ATMViewMode.LIST -> {
                    switchToMode(ATMViewMode.LIST)
                    ATMViewMode.MAP
                }
            }
        }

        buttonRedeemOnly.setOnClickListener {
            if (atmList.isNotEmpty() && map != null) {
                loadingView.visibility = View.VISIBLE

                buttonRedeemOnly.isEnabled = false
                if (isAllAtms) {
                    buttonRedeemOnly.text = "Show All"
                    isAllAtms = false
                    atmMode = ATMMode.REDEMPTION
                } else {
                    buttonRedeemOnly.text = "Show Redeem Only"
                    isAllAtms = true
                    atmMode = ATMMode.ALL
                }

                proceedToAddMarkers(it.context, map!!, atmList, atmMode)
                populateList(atmList, atmMode)
            }
        }
    }

    private fun populateList(list:List<AtmMachine>, mode:ATMMode){
        fastAdapter.clear()
        list.forEach {
            if (mode == ATMMode.REDEMPTION) {
                if (it.redemption == 1) {
                    fastAdapter.add(AtmItem(it))
                }
            } else {
                fastAdapter.add(AtmItem(it))
            }
        }
    }

    private fun proceedToAddMarkers(context:Context, map:GoogleMap, atmList:List<AtmMachine>, atmMode:ATMMode) {
        var filteredList = atmList
        if (atmMode == ATMMode.REDEMPTION) {
            filteredList = atmList.filterList {
                this.redemption == 1
            }
        }

        if (enableMapClusters && filteredList.size > clustersThreshold) {
            addAtmMarkersWithCluster(context, map, filteredList)
        } else if (filteredList.isNotEmpty()) {
            addAtmMarkers(map, filteredList)
        }

        buttonRedeemOnly.isEnabled = true
        loadingView.visibility = View.GONE
    }

    private fun addAtmMarkers(map:GoogleMap, list:List<AtmMachine>) {
        map.clear()
        list.forEach { atm ->
            val markerOpt = AtmMarker.getMarker(appContext, atm)
            val marker = map.addMarker(markerOpt)
            marker.tag = atm
        }

        map.moveCamera(CameraUpdateFactory.newLatLng(texas))
        map.animateCamera(CameraUpdateFactory.zoomTo(initialZoom))
    }

    private fun addAtmMarkersWithCluster(context: Context, map:GoogleMap, list:List<AtmMachine>){
        val clusterManager = ClusterManager<ClusteredAtm>(context, map)
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)
        clusterManager.setOnClusterItemInfoWindowClickListener {
            //TODO the click event is not implemented
        }

        clusterManager.renderer = AtmClusterRenderer(context, map, clusterManager)
        list.forEach {
            val atm = ClusteredAtm(AtmMarkerInfo(it))
            clusterManager.addItem(atm)
        }
    }

    private fun switchToMode(mode: ATMViewMode){
        when (mode) {
            ATMViewMode.MAP -> {
                listMapButton.setImageResource(R.drawable.ic_view_list)
                atmRecyclerList.visibility = View.GONE
                mapFragment.visibility = View.VISIBLE
            }
            ATMViewMode.LIST -> {
                listMapButton.setImageResource(R.drawable.ic_map_marker)
                atmRecyclerList.visibility = View.VISIBLE
                mapFragment.visibility = View.GONE
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.singleStateObserve(this) { state ->
            if (state is RequestState.LOADING) {

            }

            when (state) {
                is RequestState.Success -> {
                    atmList = state.result as List<AtmMachine>
                    map?.let { mapObject ->
                        proceedToAddMarkers(appContext, mapObject, atmList, atmMode)
                    } ?: run {
                        Timber.e("Map not ready to load markers")
                    }
                    populateList(atmList, atmMode)
                }

                is RequestState.Error -> {

                }
            }
        }
    }

    private fun prepareMap(context : Context) {
        AtmMapHelper.addMapFragment(parentFragmentManager, R.id.mapFragment, "ATMS_MAP")
            .getMapAsync { googleMap ->
                googleMap?.let {
                    Timber.d("Map prepared")
                    map = it

                    it.uiSettings.isMapToolbarEnabled = false
                    it.uiSettings.isMyLocationButtonEnabled = true
                    it.uiSettings.isZoomControlsEnabled = true
                    it.uiSettings.isZoomGesturesEnabled = true

                    it.setOnMarkerClickListener {
                        false
                    }

                    it.setOnInfoWindowClickListener { info ->
                        processInfoWindowClicked(context, info)
                    }
                    Timber.d("atm list size {$atmList.size}")
                    proceedToAddMarkers(context, it, atmList, atmMode)
                }
            }
    }

    private fun processInfoWindowClicked(context:Context, marker: Marker) {
        val atm = marker.tag as AtmMachine
        if (atm.redemption == 1) {
            moveToVerification(atm)
        } else {
            Toast.makeText(context, "This ATM does support only to buy," +
                    " redemption is still not supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToVerification(atm:AtmMachine) {

    }

}
