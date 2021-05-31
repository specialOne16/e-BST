package com.bangkit142.ebst.data

import com.google.gson.annotations.SerializedName

data class MLRequest(

    @field:SerializedName("filename")
    val filename: String,

    @field:SerializedName("image")
    val image: String,

)
