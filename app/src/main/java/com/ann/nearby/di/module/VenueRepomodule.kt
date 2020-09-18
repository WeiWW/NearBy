package com.ann.nearby.di.module

import com.ann.nearby.repo.VenueRepo
import com.ann.nearby.repo.VenueRepoImpl
import org.koin.dsl.module

val venueRepoModule = module {
    single <VenueRepo>{ VenueRepoImpl() }
}