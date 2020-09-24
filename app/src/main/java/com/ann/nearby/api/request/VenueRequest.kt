package com.ann.nearby.api.request

import com.mapbox.mapboxsdk.geometry.LatLng

data class VenueRequest (
    val radius: Double,
    val latLng: LatLng
)