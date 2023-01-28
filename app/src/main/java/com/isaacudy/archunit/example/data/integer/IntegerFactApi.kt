package com.isaacudy.archunit.example.data.integer

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Singleton


interface IntegerFactApi {
    @GET("{number}")
    suspend fun getFact(@Path("number") number: Int): Response<String>
}

@Module
@InstallIn(SingletonComponent::class)
class IntegerFactApiModule {
    @Provides
    @Singleton
    fun provideIntegerFactApi(): IntegerFactApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://numbersapi.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        return retrofit.create(IntegerFactApi::class.java)
    }
}