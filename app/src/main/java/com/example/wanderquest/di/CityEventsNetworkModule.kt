package com.example.wanderquest.di

import com.example.wanderquest.BuildConfig
import com.example.wanderquest.apiclient.ICityEventsClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CityEventsRetrofit

@Module
@InstallIn(SingletonComponent::class)
class CityEventsNetworkModule {

    @Provides
    @Singleton
    @CityEventsRetrofit
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.predicthq.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient())
            .build()
    }

    @Provides
    @Singleton
    fun provideICityEventsClient(@CityEventsRetrofit retrofit: Retrofit): ICityEventsClient {
        return retrofit.create(ICityEventsClient::class.java)
    }

    private fun okhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Authorization", "Bearer ${BuildConfig.EVENTS_API}")
        return chain.proceed(requestBuilder.build())
    }
}