package com.ann.nearby.repo

import com.ann.nearby.api.ApiService
import com.ann.nearby.api.response.Location
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.api.response.VenueDetailsResponse
import com.ann.nearby.utils.NetworkHelper
import com.mapbox.mapboxsdk.geometry.LatLng
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
    private val networkHelper: NetworkHelper by inject()
    private val browseVenuesResults = ConflatedBroadcastChannel<List<Venue>>()
    private val venueDetailResult = ConflatedBroadcastChannel<VenueDetail?>()
    private val cacheVenuesMap:LinkedHashMap<LatLng,Venue> = linkedMapOf()

    @OptIn(FlowPreview::class)
    override suspend fun getVenueList(filter: Map<String, String>): Flow<List<Venue>> {
        requestVenueList(filter)
        return browseVenuesResults.asFlow()
    }

    private suspend fun requestVenueList(filter: Map<String, String>){
        if (!networkHelper.isNetworkConnected()) return

        val response = apiService.browseNearByVenues(filter)
        if (!response.isSuccessful || response.body() == null) return
        response.body()?.let { venueListResponse ->
            val venueList = venueListResponse.response.venues
            addToCache(venueList)
            browseVenuesResults.offer(venueList)
        }
    }

    private fun addToCache(list: List<Venue>){
        for (venue in list){
            val key = venue.location.let{location: Location ->
                LatLng(location.lat,location.lng)
            }
            if (!cacheVenuesMap.containsKey(key)){
                cacheVenuesMap[key] = venue
            }
        }
    }

    @OptIn(FlowPreview::class)
    override suspend fun getVenueDetail(latLng: LatLng, filter: Map<String, String>): Flow<VenueDetail?> {
        val venue = cacheVenuesMap[latLng]
        venue?.let {
            requestVenueDetail(it,filter)
        }
        return venueDetailResult.asFlow()
    }

    private suspend fun requestVenueDetail(venue: Venue, filter: Map<String, String>){
        if (!networkHelper.isNetworkConnected()) return

        val response = apiService.getVenueDetail(venue.id, filter)
        if (!response.isSuccessful || response.body() == null) return
        response.body()?.let { venueDetailResponse: VenueDetailsResponse ->
            val detail = venueDetailResponse.response.venue
            venueDetailResult.offer(detail)
        }
    }

}
