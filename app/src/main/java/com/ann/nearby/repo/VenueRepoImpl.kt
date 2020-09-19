package com.ann.nearby.repo

import com.ann.nearby.api.ApiService
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.api.response.VenueDetailsResponse
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
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
    private val browseVenuesResults = ConflatedBroadcastChannel<List<Feature>>()
    private val venueDetailResult = ConflatedBroadcastChannel<VenueDetail?>()

    @OptIn(FlowPreview::class)
    override suspend fun getVenueList(filter: Map<String, String>): Flow<List<Feature>> {
        val response = apiService.browseNearByVenues(filter)
        //TODO response error and check network state
        if (response.isSuccessful){
            response.body()?.let { venueListResponse ->
                val venueList = venueListResponse.response.venues
                val list = transferToFeartures(venueList)
                browseVenuesResults.offer(list)
            }
        }else{
            browseVenuesResults.offer(emptyList())
        }
        return browseVenuesResults.asFlow()
    }

    private fun transferToFeartures(venueList: List<Venue>): List<Feature> {
        val featureList = mutableListOf<Feature>()
        for (venue in venueList){
            val feature = Feature.fromGeometry(Point.fromLngLat(venue.location.lng, venue.location.lat))
            featureList.add(feature)
        }
        return featureList.toList()
    }

    @OptIn(FlowPreview::class)
    override suspend fun getVenueDetail(venueId: String, filter: Map<String, String>): Flow<VenueDetail?> {
        val response = apiService.getVenueDetail(venueId, filter)
        //TODO response error and check network state
        if (response.isSuccessful){
            response.body()?.let { venueDetailResponse: VenueDetailsResponse ->
                val detail = venueDetailResponse.response.venue
                venueDetailResult.offer(detail)
            }
        }else{
            venueDetailResult.offer(null)
        }
        return venueDetailResult.asFlow()
    }

}
