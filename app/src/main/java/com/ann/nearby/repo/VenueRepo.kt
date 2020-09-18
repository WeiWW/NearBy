package com.ann.nearby.repo

import com.ann.nearby.api.response.Venue
import kotlinx.coroutines.flow.Flow

interface VenueRepo {
    suspend fun getVenueList(filter: Map<String, String>): Flow<List<Venue>>
    fun getVenueDetail(venueId: String)
}