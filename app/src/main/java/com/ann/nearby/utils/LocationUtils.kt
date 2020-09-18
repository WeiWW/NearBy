package com.ann.nearby.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import com.mapbox.mapboxsdk.maps.Style

private const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
private const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

fun getLocationsComponentActivationOptions(context: Context, style: Style): LocationComponentActivationOptions
        = LocationComponentActivationOptions.builder(context,style)
    .useDefaultLocationEngine(false)
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

fun buildDefaultMapOptions(context: Context,attrs:AttributeSet?) = MapboxMapOptions.createFromAttributes(context, attrs)
    .camera(CameraPosition.Builder().zoom(12.0).build())