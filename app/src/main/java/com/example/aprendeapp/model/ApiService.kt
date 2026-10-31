package com.example.aprendeapp.model

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("recursos")
    suspend fun getResources(): Response<List<Resource>>

    @POST("recursos")
    suspend fun addResource(@Body resource: Resource): Response<Resource>

    @PUT("recursos/{id}")
    suspend fun updateResource(@Path("id") id: Int, @Body resource: Resource): Response<Resource>

    @DELETE("recursos/{id}")
    suspend fun deleteResource(@Path("id") id: Int): Response<Void>
}