package com.ann.nearby.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.AttributeSet
import com.ann.nearby.R
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

private const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
private const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
const val SOURCE_ID = "SOURCE_ID"
private const val ICON_ID = "ICON_ID"
const val LAYER_ID = "LAYER_ID"

fun getLocationsComponentActivationOptions(
    context: Context,
    style: Style
): LocationComponentActivationOptions = LocationComponentActivationOptions.builder(context, style)
    .useDefaultLocationEngine(true)
    .build()

val locationEngineRequest: LocationEngineRequest =
    LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
        .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
        .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
        .build()

@SuppressLint("MissingPermission")
fun registerLocationEngineCallback(context: Context, request: LocationEngineRequest, callback: LocationEngineCallback<LocationEngineResult>, looper: Looper)
        = LocationEngineProvider.getBestLocationEngine(context).apply {
    this.requestLocationUpdates(request, callback, looper)
    this.getLastLocation(callback)
}

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

fun buildDefaultMapOptions(context: Context, attrs: AttributeSet?) =
    MapboxMapOptions.createFromAttributes(context, attrs)
        .camera(CameraPosition.Builder().zoom(16.0).build())

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
    .withImage(ICON_ID, BitmapFactory.decodeResource(
        context.resources,
        R.drawable.mapbox_marker_icon_default
    ))