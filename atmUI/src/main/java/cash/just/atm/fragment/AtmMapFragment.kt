package cash.just.atm.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cash.just.atm.AtmItem
import cash.just.atm.AtmMapHelper
import cash.just.atm.R
import cash.just.atm.base.RequestState
import cash.just.atm.base.showError
import cash.just.atm.model.AtmClusterRenderer
import cash.just.atm.model.AtmMarker
import cash.just.atm.model.AtmMarkerInfo
import cash.just.atm.model.ClusteredAtm
import cash.just.atm.viewmodel.AtmViewModel
import cash.just.sdk.model.AtmMachine
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.square.project.base.singleStateObserve
import kotlinx.android.synthetic.main.fragment_map.*
import okhttp3.internal.filterList
import timber.log.Timber

private enum class ViewMode {
    MAP,
    LIST
}

private enum class MachineMode {
    ALL,
    REDEMPTION
}

class AtmMapFragment : Fragment() {
    companion object {
        private val texas = LatLng(31.000000, -100.000000)
        private const val MAP_FRAGMENT_TAG = "AtmMapFragment"
        private const val initialZoom = 5f
        private const val enableMapClusters = false
        private const val clustersThreshold = 100
    }

    private lateinit var appContext:Context
    private val viewModel = AtmViewModel()

    private var viewMode: ViewMode = ViewMode.MAP
    private var atmMode: MachineMode = MachineMode.ALL

    private var map: GoogleMap? = null
    private var atmList: List<AtmMachine> = ArrayList()
    private var isAllMachines = true
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
                switchToMode(ViewMode.LIST)
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
                ViewMode.MAP -> {
                    switchToMode(ViewMode.MAP)
                    ViewMode.LIST
                }
                ViewMode.LIST -> {
                    switchToMode(ViewMode.LIST)
                    ViewMode.MAP
                }
            }
        }

        buttonRedeemOnly.setOnClickListener {
            if (atmList.isNotEmpty() && map != null) {
                loadingView.visibility = View.VISIBLE

                buttonRedeemOnly.isEnabled = false
                if (isAllMachines) {
                    buttonRedeemOnly.text = "Show All"
                    isAllMachines = false
                    atmMode = MachineMode.REDEMPTION
                } else {
                    buttonRedeemOnly.text = "Show Redeem Only"
                    isAllMachines = true
                    atmMode = MachineMode.ALL
                }

                proceedToAddMarkers(it.context, map!!, atmList, atmMode)
                populateList(atmList, atmMode)
            }
        }
    }

    private fun populateList(list:List<AtmMachine>, mode: MachineMode){
        fastAdapter.clear()
        list.forEach {
            if (mode == MachineMode.REDEMPTION) {
                if (it.redemption == 1) {
                    fastAdapter.add(AtmItem(it))
                }
            } else {
                fastAdapter.add(AtmItem(it))
            }
        }
    }

    private fun proceedToAddMarkers(context:Context, map:GoogleMap, atmList:List<AtmMachine>, atmMode: MachineMode) {
        var filteredList = atmList
        if (atmMode == MachineMode.REDEMPTION) {
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

    private fun switchToMode(mode: ViewMode){
        when (mode) {
            ViewMode.MAP -> {
                listMapButton.setImageResource(R.drawable.ic_view_list)
                atmRecyclerList.visibility = View.GONE
                mapFragment.visibility = View.VISIBLE
            }
            ViewMode.LIST -> {
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
                    @Suppress("UNCHECKED_CAST")
                    atmList = state.result as List<AtmMachine>
                    map?.let { mapObject ->
                        proceedToAddMarkers(appContext, mapObject, atmList, atmMode)
                    } ?: run {
                        Timber.e("Map not ready to load markers")
                    }
                    populateList(atmList, atmMode)
                }

                is RequestState.Error -> {
                    showError(this, state.throwable)
                }
            }
        }
    }

    private fun prepareMap(context : Context) {
        AtmMapHelper.addMapFragment(childFragmentManager, R.id.mapFragment, MAP_FRAGMENT_TAG)
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
        findNavController().navigate(AtmMapFragmentDirections.mapToRequest(atm))
    }
}
