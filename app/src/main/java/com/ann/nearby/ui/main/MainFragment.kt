package com.ann.nearby.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ann.nearby.BuildConfig
import com.ann.nearby.R
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.utils.*
import com.bumptech.glide.Glide
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import kotlinx.android.synthetic.main.info_cardview.view.*
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(), PermissionsListener, MapboxMap.OnMoveListener,MapboxMap.OnMapClickListener {

    companion object {
        fun newInstance() = MainFragment()
        const val TAG = "MAIN_FRAGMENT"
    }

    private val viewModel: MainViewModel by viewModel()
    private lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var symbolManager: SymbolManager
    private var previousCameraPosition:LatLng = LatLng(0.0,0.0,0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(
            this.requireContext(),
            BuildConfig.MAPBOX_ACCESS_TOKEN
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.addOnMoveListener(this)
            mapboxMap.addOnMapClickListener(this)
            mapboxMap.setStyle(
                mapStyle(requireContext())
            ) { style ->
                enableLocation(mapboxMap, style)

                symbolManager = SymbolManager(mapView,mapboxMap,style).apply {
                    this.iconAllowOverlap = true
                    this.textAllowOverlap = true
                }

                symbolManager.addClickListener(OnSymbolClickListener { symbol ->
                    viewModel.queryLiveData.postValue(symbol.latLng)
                    true
                })
            }
        }

        focusLocation.setOnClickListener {
            mapboxMap.locationComponent.lastKnownLocation?.let {
                animateCamera(mapboxMap,it)
                viewModel.locationLiveData.postValue(latLngFormat(it))
            }
        }
        viewModel.venues.observeForever {
            symbolManager.deleteAll()
            //display venues
            symbolManager.create(it)
        }

        viewModel.venueDetail.observeForever {
            it?.let {venueDetail:VenueDetail->
                venueCard.visibility = View.VISIBLE
                venueCard.name.text = venueDetail.name
                venueCard.description.text = venueDetail.categories?.get(0)?.name
                venueCard.score.text = venueDetail.rating?.toString()
                venueCard.openTime.text = venueDetail.hours?.status

                Glide.with(venueCard.image).load(R.drawable.ic_image).into(venueCard.image)
                venueDetail.bestPhoto?.let {bestPhoto ->
                    val widthPx = venueCard.image.width
                    val heightPx = venueCard.image.height
                    val imgUrl = bestPhoto.prefix + "${widthPx}x${heightPx}"+bestPhoto.suffix
                    Glide.with(venueCard.image)
                        .load(imgUrl)
                        .error(R.drawable.ic_image)
                        .into(venueCard.image)
                        .clearOnDetach()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation(mapboxMap: MapboxMap, style: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {

            enableLocationComponent(requireContext(), mapboxMap, style)
            //camera focus device current location
            mapboxMap.locationComponent.lastKnownLocation?.let {
                animateCamera(mapboxMap,it)
                viewModel.locationLiveData.postValue(latLngFormat(it))
            }

        } else {
            permissionsManager = PermissionsManager(this).apply {
                this.requestLocationPermissions(activity)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(
            requireContext(), R.string.user_location_permission_explanation,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            this.mapboxMap.getStyle { style -> enableLocation(this.mapboxMap, style) }
        } else {
            Toast.makeText(
                requireContext(),
                R.string.user_location_permission_not_granted,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun onMoveBegin(detector: MoveGestureDetector) {
        venueCard.visibility = View.GONE
        this.previousCameraPosition = this.mapboxMap.cameraPosition.target
    }

    override fun onMove(detector: MoveGestureDetector) {}

    override fun onMoveEnd(detector: MoveGestureDetector) {
        val lastLatlng = this.mapboxMap.cameraPosition.target
        if (isDistanceLargerHalfRadius(previousCameraPosition,lastLatlng)){
            viewModel.locationLiveData.postValue(lastLatlng)
        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        venueCard.visibility = View.GONE
        return false
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}