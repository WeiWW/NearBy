package com.ann.nearby.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import com.ann.nearby.R
import com.ann.nearby.api.RADIUS_BASE
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.mapbox.turf.TurfTransformation

private const val SOURCE_ID = "SOURCE_ID"
private const val ICON_ID = "ICON_ID"
private const val LAYER_ID = "LAYER_ID"

fun Style.getLocationsComponentActivationOptions(
    context: Context,
): LocationComponentActivationOptions = LocationComponentActivationOptions.builder(context, this)
    .useDefaultLocationEngine(true)
    .build()

@SuppressLint("MissingPermission")
fun MapboxMap.enableLocationComponent(context: Context, style: Style) =
    this.locationComponent.run {
        // Activate with the LocationComponentActivationOptions object
        this.activateLocationComponent(style.getLocationsComponentActivationOptions(context))
        this.isLocationComponentEnabled = true
        // Set the component's camera mode
        this.cameraMode = CameraMode.TRACKING
        // Set the component's render mode
        this.renderMode = RenderMode.COMPASS
    }

fun Style.Builder.defaultStyle(context: Context) = this
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
        ICON_ID, BitmapFactory.decodeResource(
            context.resources, R.drawable.mapbox_marker_icon_default
        )
    ).withTransition(TransitionOptions(0, 0, false))

fun newSymbol(latitude: Double, longitude: Double): SymbolOptions = SymbolOptions()
    .withLatLng(LatLng(latitude, longitude))
    .withIconImage(ICON_ID)
    .withIconSize(1.3f)
    .withDraggable(false)

fun Location.latLng() = LatLng(this)

fun MapboxMap.animateCamera(location: Location){
    val latLng = location.latLng()
    val position
            = CameraPosition.Builder()
        .target(latLng)
        .zoom(16.0)
        .tilt(10.0).build()
    this.animateCamera(CameraUpdateFactory.newCameraPosition(position))
}

fun LatLng.point(): Point = Point.fromLngLat(this.longitude, this.latitude)

fun Point.distance(point: Point) = TurfMeasurement.distance(this, point,
    TurfConstants.UNIT_METERS
)

/**
 * The radius range is 450~1250 (meter), depends on camera's zoom
 * */
fun radius(zoom: Double) = RADIUS_BASE.times(25 - zoom)


fun Point.getTurfPolygon(
    radius: Double
): Polygon {
    return TurfTransformation.circle(this, radius , 360, TurfConstants.UNIT_METERS)
}

fun MapboxMap.fixArea(point: Point) {
    val radius = radius(this.cameraPosition.zoom)
    val polygonArea = point.getTurfPolygon(radius)

    this.getStyle {
        it.getLayerAs<SymbolLayer>(LAYER_ID)?.setFilter(Expression.within(polygonArea))
    }
}
