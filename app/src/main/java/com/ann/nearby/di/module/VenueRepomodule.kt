package com.ann.nearby.di.module

import com.ann.nearby.repo.VenueRepo
import com.ann.nearby.repo.VenueRepoImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val venueRepoModule = module {
    single <VenueRepo>{ VenueRepoImpl() }
}