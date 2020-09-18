package com.ann.nearby.repo

import com.ann.nearby.api.ApiService
import com.ann.nearby.api.response.Venue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
class VenueRepoImpl:VenueRepo,KoinComponent {
    private val apiService: ApiService by inject()
    private val browseVenuesResults = ConflatedBroadcastChannel<List<Venue>>()

    @OptIn(FlowPreview::class)
    override suspend fun getVenueList(filter: Map<String, String>): Flow<List<Venue>> {
        val response = apiService.browseNearByVenues(filter)
        //TODO response error and check network state
        if (response.isSuccessful){
            response.body()?.let {venueListResponse ->
                val venueList = venueListResponse.response.venues
                browseVenuesResults.offer(venueList)
            }
        }
        return browseVenuesResults.asFlow()
    }

    override fun getVenueDetail(venueId: String) {

    }

}
