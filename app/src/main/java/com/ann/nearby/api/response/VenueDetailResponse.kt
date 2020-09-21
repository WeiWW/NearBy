package com.ann.nearby.api.response

import com.squareup.moshi.Json

data class VenueDetailsResponse(
    val meta: Meta,
    val response: Response
) {
    data class Response(
        @field:Json(name = "venue")
        val venue: VenueDetail
    )
}

data class VenueDetail(
    val bestPhoto: BestPhoto?,
    val categories: List<Category>?,
    val hours: Hours?,
    val id: String,
    val name: String,
    val rating: Double?,
)

data class Hours(
    val status: String,
)

data class BestPhoto(
    val prefix: String,
    val suffix: String,
)

data class Category(
    val id: String,
    val name: String
)