package com.ann.nearby.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import com.ann.nearby.R
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

const val SOURCE_ID = "SOURCE_ID"
private const val ICON_ID = "ICON_ID"
const val LAYER_ID = "LAYER_ID"

fun getLocationsComponentActivationOptions(
    context: Context,
    style: Style
): LocationComponentActivationOptions = LocationComponentActivationOptions.builder(context, style)
    .useDefaultLocationEngine(true)
    .build()

@SuppressLint("MissingPermission")
fun enableLocationComponent(context: Context, mapboxMap: MapboxMap, style: Style) =
    mapboxMap.locationComponent.apply {
        // Activate with the LocationComponentActivationOptions object
        this.activateLocationComponent(getLocationsComponentActivationOptions(context,style))
        this.isLocationComponentEnabled = true
        // Set the component's camera mode
        this.cameraMode = CameraMode.TRACKING
        // Set the component's render mode
        this.renderMode = RenderMode.COMPASS
    }

fun mapStyle(context: Context) = Style.Builder()
    .fromUri(Style.LIGHT)
    .withSource(GeoJsonSource(SOURCE_ID))
    .withLayer(
        SymbolLayer(LAYER_ID, SOURCE_ID)
            .withProperties(
                PropertyFactory.iconImage(ICON_ID),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
            )
    )
    .withImage(
        ICON_ID, BitmapFactory.decodeResource(context.resources, R.drawable.mapbox_marker_icon_default
        )
    )

fun newSymbol(latitude: Double, longitude: Double): SymbolOptions = SymbolOptions()
    .withLatLng(LatLng(latitude, longitude))
    .withIconImage(ICON_ID)
    .withIconSize(1.3f)
    .withDraggable(false)

fun locationFormat(latLng: LatLng) = Location(LocationManager.PASSIVE_PROVIDER).apply {
    this.latitude = latLng.latitude
    this.longitude = latLng.longitude
    this.altitude = latLng.altitude
}

fun latLngFormat(location:Location) = LatLng(location)

fun animateCamera(mapboxMap: MapboxMap,location: Location){
    val latLng = latLngFormat(location)
    val position = CameraPosition.Builder().target(latLng).zoom(16.0).tilt(10.0).build()
    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
}