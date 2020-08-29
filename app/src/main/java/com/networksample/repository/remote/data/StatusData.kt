package com.networksample.repository.remote.data

import com.google.gson.annotations.SerializedName


data class StatusData(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)