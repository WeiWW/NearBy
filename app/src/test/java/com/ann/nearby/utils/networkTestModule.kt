package com.ann.nearby.utils

import com.ann.nearby.api.ApiService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun networkTestModule(url: String) = module {
    single { provideRetrofit(url) }
    factory { get<Retrofit>().create(ApiService::class.java) }
}

private fun provideRetrofit(url: String): Retrofit = Retrofit.Builder()
    .addConverterFactory(NullOnEmptyConverterFactory())
    .addConverterFactory(MoshiConverterFactory.create())
    .baseUrl(url)
    .build()