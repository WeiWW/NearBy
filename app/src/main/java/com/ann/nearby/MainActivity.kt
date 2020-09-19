package com.ann.nearby

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ann.nearby.ui.main.MainViewModel
import com.ann.nearby.utils.SOURCE_ID
import com.ann.nearby.utils.buildDefaultMapOptions
import com.ann.nearby.utils.enableLocationComponent
import com.ann.nearby.utils.mapStyle
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), PermissionsListener,MapboxMap.OnMoveListener{
    private val TAG = this.javaClass.simpleName
    private val viewModel: MainViewModel by inject()
    private val MAPFRAGMENT_TAG = "com.mapbox.map"
    private lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        Mapbox.getInstance(
            this,
            BuildConfig.MAPBOX_ACCESS_TOKEN
        )

        val mapFragment: SupportMapFragment?
        if (savedInstanceState == null) {
            // Create map fragment
            mapFragment = SupportMapFragment.newInstance(buildDefaultMapOptions(this,null))
            // Add map fragment to parent container
            supportFragmentManager.beginTransaction()
                .add(R.id.container, mapFragment, MAPFRAGMENT_TAG)
                .commit()
        } else {
            mapFragment =
                supportFragmentManager.findFragmentByTag(MAPFRAGMENT_TAG) as SupportMapFragment?
        }

        mapFragment?.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.addOnMoveListener(this)

            mapboxMap.setStyle(
                mapStyle(this)
            ) { style -> enableLocation(mapboxMap, style) }
        }

        viewModel.venues.observeForever {
            mapboxMap.style?.getSourceAs<GeoJsonSource>(SOURCE_ID)?.setGeoJson(FeatureCollection.fromFeatures(it))
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation(mapboxMap: MapboxMap, style: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            enableLocationComponent(this, mapboxMap, style)

        } else {
            permissionsManager = PermissionsManager(this).apply {
                this.requestLocationPermissions(this@MainActivity)
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
            this, R.string.user_location_permission_explanation,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            this.mapboxMap.getStyle { style -> enableLocation(this.mapboxMap, style) }
        } else {
            Toast.makeText(
                this,
                R.string.user_location_permission_not_granted,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun onMoveBegin(detector: MoveGestureDetector) {}

    override fun onMove(detector: MoveGestureDetector) {}

    override fun onMoveEnd(detector: MoveGestureDetector) {
        val latlng = this.mapboxMap.cameraPosition.target
        val location = Location(LocationManager.PASSIVE_PROVIDER)
        location.latitude = latlng.latitude
        location.longitude = latlng.longitude
        location.altitude = latlng.altitude
        viewModel.locationLiveData.postValue(location)
    }
}