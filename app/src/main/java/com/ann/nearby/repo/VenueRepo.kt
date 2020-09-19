package com.ann.nearby.repo

import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.mapbox.geojson.Feature
import kotlinx.coroutines.flow.Flow

interface VenueRepo {
    suspend fun getVenueList(filter: Map<String, String>): Flow<List<Feature>>
    suspend fun getVenueDetail(venueId: String, filter: Map<String, String>):Flow<VenueDetail?>
}