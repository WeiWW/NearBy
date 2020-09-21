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
    val id: String,
    val location: Location
)

data class Location(
    val distance: Int,
    val lat: Double,
    val lng: Double,
)