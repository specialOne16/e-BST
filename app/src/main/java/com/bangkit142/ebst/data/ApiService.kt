package com.bangkit142.ebst.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("predict")
    fun getVerdict(@Body body: MLRequest): Call<List<MLResponse>>

}