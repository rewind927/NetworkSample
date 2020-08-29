package com.networksample.repository.remote.api

import com.networksample.repository.remote.data.StatusData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface PublicAPIService {
    @GET
    fun status(@Url url: String): Call<StatusData>
}