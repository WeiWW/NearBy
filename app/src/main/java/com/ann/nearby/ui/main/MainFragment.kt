package com.ann.nearby.ui.main

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ann.nearby.BuildConfig
import com.ann.nearby.R
import com.ann.nearby.utils.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(), PermissionsListener, MapboxMap.OnMoveListener{

    companion object {
        fun newInstance() = MainFragment()
        const val TAG = "MAIN_FRAGMENT"
    }

    private val viewModel: MainViewModel by viewModel()
    private lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
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
            mapboxMap.setStyle(
                mapStyle(requireContext())
            ) { style ->
                enableLocation(mapboxMap, style)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation(mapboxMap: MapboxMap, style: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {

            enableLocationComponent(requireContext(), mapboxMap, style)
            mapboxMap.locationComponent.lastKnownLocation?.let {
                val latLng = LatLng(it.latitude,it.longitude)
                val position = CameraPosition.Builder().target(latLng).zoom(13.0).tilt(10.0).build()
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
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

    override fun onMoveBegin(detector: MoveGestureDetector) {}

    override fun onMove(detector: MoveGestureDetector) {}

    override fun onMoveEnd(detector: MoveGestureDetector) {
        //TODO: Not query too often
        val latlng = this.mapboxMap.cameraPosition.target
        val location = Location(LocationManager.PASSIVE_PROVIDER)
        location.latitude = latlng.latitude
        location.longitude = latlng.longitude
        location.altitude = latlng.altitude
        viewModel.locationLiveData.postValue(location)
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