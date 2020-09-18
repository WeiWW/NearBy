package com.ann.nearby.api

import com.ann.nearby.api.response.VenueDetailsResponse
import com.ann.nearby.api.response.VenueListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap


interface ApiService {
    @GET("venues/search")
    fun browseNearByRestaurant(@QueryMap filter: Map<String, String>): Response<VenueListResponse>

    @GET("venues/{VENUE_ID}")
    fun getVenueDetail(
        @Path("VENUE_ID") venueId: String,
        @QueryMap filter: Map<String, String>
    ): Response<VenueDetailsResponse>
}