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
        ICON_ID, BitmapFactory.decodeResource(
            context.resources, R.drawable.mapbox_marker_icon_default
        )
    ).withTransition(TransitionOptions(0, 0, false))

fun newSymbol(latitude: Double, longitude: Double): SymbolOptions = SymbolOptions()
    .withLatLng(LatLng(latitude, longitude))
    .withIconImage(ICON_ID)
    .withIconSize(1.3f)
    .withDraggable(false)

fun latLngFormat(location:Location) = LatLng(location)

fun animateCamera(mapboxMap: MapboxMap,location: Location){
    val latLng = latLngFormat(location)
    val position = CameraPosition.Builder().target(latLng).zoom(16.0).tilt(10.0).build()
    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
}

fun getPointFromLatlng(latLng: LatLng): Point = Point.fromLngLat(latLng.longitude, latLng.latitude)
fun getDistanceFromPoints(point1: Point, point2: Point) = TurfMeasurement.distance(
    point1, point2,
    TurfConstants.UNIT_METERS
)

fun getDistanceFromLanLngs(lanLng1: LatLng, latLng2: LatLng): Double {
    val point1 = getPointFromLatlng(lanLng1)
    val point2 = getPointFromLatlng(latLng2)
    return getDistanceFromPoints(point1, point2)
}

fun radius(zoom: Double) = run {
    RADIUS_BASE.times(25 - zoom)
}

fun getTurfPolygon(
    centerPoint: Point, radius: Double
): Polygon {
    return TurfTransformation.circle(centerPoint, radius / 10, 360, TurfConstants.UNIT_KILOMETERS)
}

fun fixArea(latLng: LatLng, mapboxMap: MapboxMap) {
    val radius = radius(mapboxMap.cameraPosition.zoom)
    val polygonArea = getTurfPolygon(getPointFromLatlng(latLng), radius)

    mapboxMap.getStyle {
        it.getLayerAs<SymbolLayer>(LAYER_ID)?.setFilter(Expression.within(polygonArea))
    }
}
