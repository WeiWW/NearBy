package com.ann.nearby.di.module

import android.content.Context
import com.ann.nearby.BuildConfig
import com.ann.nearby.api.ApiService
import com.ann.nearby.utils.NetworkHelper
import com.ann.nearby.utils.NetworkHelperImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideNetworkHelper(androidContext()) }
    factory { get<Retrofit>().create(ApiService::class.java) }
}

private fun provideNetworkHelper(context: Context): NetworkHelper = NetworkHelperImpl(context)

private fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
} else OkHttpClient
    .Builder()
    .build()

private fun provideRetrofit(
    okHttpClient: OkHttpClient
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BuildConfig.FOURSQUARE_API_DOMANIN)
        .client(okHttpClient)
        .build()