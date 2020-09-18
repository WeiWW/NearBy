package com.ann.nearby.repo

import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import kotlinx.coroutines.flow.Flow

interface VenueRepo {
    suspend fun getVenueList(filter: Map<String, String>): Flow<List<Venue>>
    suspend fun getVenueDetail(venueId: String, filter: Map<String, String>):Flow<VenueDetail?>
}