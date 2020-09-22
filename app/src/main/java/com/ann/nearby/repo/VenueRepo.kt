package com.ann.nearby.repo

import com.ann.nearby.api.response.VenueDetail
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import kotlinx.coroutines.flow.Flow

interface VenueRepo {
    suspend fun getVenueList(latLng: LatLng): Flow<List<SymbolOptions>>
    suspend fun getVenueDetail(latLng: LatLng):Flow<VenueDetail?>
}