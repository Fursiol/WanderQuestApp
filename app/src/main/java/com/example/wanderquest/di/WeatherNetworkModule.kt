package com.example.wanderquest.di

import com.example.wanderquest.apiclient.IWeatherClient
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class WeatherRetrofit
@Module
@InstallIn(SingletonComponent::class)
class WeatherNetworkModule {

    @Provides
    @Singleton
    @WeatherRetrofit
    fun provideRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
            .build()
    }

    @Provides
    @Singleton
    fun provideIWeatherClient(@WeatherRetrofit retrofit: Retrofit): IWeatherClient {
        return retrofit.create(IWeatherClient::class.java)
    }

}