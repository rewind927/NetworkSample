package com.networksample.repository

import com.networksample.repository.remote.api.PublicAPIService
import com.networksample.repository.remote.data.StatusData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StatusRepository {

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析
        .baseUrl("https://code-test.migoinc-dev.com/")
        .build()

    private val apiService: PublicAPIService = retrofit.create(PublicAPIService::class.java)

    var repositoryCallback: RepositoryCallback<StatusData>? = null

    fun getData(currentAPI: String) {
        apiService.status(currentAPI).enqueue(object : Callback<StatusData> {
            override fun onFailure(call: Call<StatusData>, t: Throwable) {
                repositoryCallback?.onFailed(t)
            }

            override fun onResponse(call: Call<StatusData>, response: Response<StatusData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        repositoryCallback?.onSuccess(it)
                    }
                }
            }

        })
    }

    interface RepositoryCallback<T> {
        fun onSuccess(data: T)
        fun onFailed(exception: Throwable)
    }

}