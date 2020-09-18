package com.ann.nearby.api

import com.ann.nearby.BuildConfig

fun nearByRestaurantQueryMap(
    latitude: String,
    longitude: String,
    radius: String
): HashMap<String, String> {
    val map = baseQueryMap
    map["intent"] = "browse"
    map["categoryId"] = "4d4b7105d754a06374d81259"
    map["ll"] = "$latitude,$longitude"
    map["radius"] = radius
    return map
}

val baseQueryMap = hashMapOf(
    "client_id" to BuildConfig.FOURSQUARE_CLIENT_ID,
    "client_secret" to BuildConfig.FOURSQUARE_CLIENT_SECRET,
    "v" to "20190425"
)