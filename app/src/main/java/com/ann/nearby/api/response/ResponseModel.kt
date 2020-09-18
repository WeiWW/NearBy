package com.ann.nearby.api.response

data class VenueListResponse(
    val meta: Meta,
    val response: Response
)

data class Meta(
    val code: Int,
    val requestId: String
)

data class Response(
    val venues: List<Venue>
)

data class Venue(
    val categories: List<Category>,
    val id: String,
    val location: Location,
    val name: String,
)

data class Category(
    val icon: Icon,
    val id: String,
    val name: String
)

data class Location(
    val distance: Int,
    val lat: Double,
    val lng: Double,
)

data class Icon(
    val prefix: String,
    val suffix: String
)

data class LabeledLatLng(
    val label: String,
    val lat: Double,
    val lng: Double
)