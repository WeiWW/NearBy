package com.ann.nearby.api

import android.location.Location
import com.ann.nearby.BuildConfig

private const val FOOD_CATEGORY = "4d4b7105d754a06374d81259"
private const val VERSION = "20190425"
/**Finds venues that the current user
 * (or, for userless requests, a typical user) is likely to checkin to at the provided ll
 * , at the current moment in time.**/
private const val CHACKIN = "checkin"
const val RADIUS = 500

fun nearByRestaurantQueryMap(
    location:Location,
    radius: String
): HashMap<String, String> {
    val map = baseQueryMap
    map["intent"] = CHACKIN
    map["categoryId"] = FOOD_CATEGORY
    map["ll"] = "${location.latitude},${location.longitude}"
    map["radius"] = RADIUS.toString()
    return map
}

val baseQueryMap = hashMapOf(
    "client_id" to BuildConfig.FOURSQUARE_CLIENT_ID,
    "client_secret" to BuildConfig.FOURSQUARE_CLIENT_SECRET,
    "v" to VERSION
)