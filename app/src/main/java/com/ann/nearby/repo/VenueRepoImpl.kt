package com.ann.nearby.repo

import com.ann.nearby.api.ApiService
import com.ann.nearby.api.response.Location
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.api.response.VenueDetailsResponse
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
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
    private val browseVenuesResults = ConflatedBroadcastChannel<List<Venue>>()
    private val venueDetailResult = ConflatedBroadcastChannel<VenueDetail?>()
    private val cacheVenuesMap:LinkedHashMap<LatLng,Venue> = linkedMapOf()

    @OptIn(FlowPreview::class)
    override suspend fun getVenueList(filter: Map<String, String>): Flow<List<Venue>> {
        val response = apiService.browseNearByVenues(filter)
        //TODO response error and check network state
        if (response.isSuccessful){
            response.body()?.let { venueListResponse ->
                val venueList = venueListResponse.response.venues
                addToCache(venueList)
                browseVenuesResults.offer(venueList)
            }
        }else{
            browseVenuesResults.offer(emptyList())
        }
        return browseVenuesResults.asFlow()
    }

    private fun addToCache(list: List<Venue>){
        for (venue in list){
            val key = venue.location.let{location: Location ->
                LatLng(location.lat,location.lng)
            }
            if (!cacheVenuesMap.containsKey(key)){
                cacheVenuesMap.put(key,venue)
            }
        }
    }

    private fun transferToFeatures(venueList: List<Venue>): List<Feature> {
        val featureList = mutableListOf<Feature>()
        for (venue in venueList){
            val feature = Feature.fromGeometry(Point.fromLngLat(venue.location.lng, venue.location.lat))
            featureList.add(feature)
        }
        return featureList.toList()
    }

    @OptIn(FlowPreview::class)
    override suspend fun getVenueDetail(latLng: LatLng, filter: Map<String, String>): Flow<VenueDetail?> {
        val venue = cacheVenuesMap[latLng]
        venue?.let {
            val response = apiService.getVenueDetail(it.id, filter)
            //TODO response error and check network state
            if (response.isSuccessful){
                response.body()?.let { venueDetailResponse: VenueDetailsResponse ->
                    val detail = venueDetailResponse.response.venue
                    venueDetailResult.offer(detail)
                }
            }
        }
        return venueDetailResult.asFlow()
    }

}
