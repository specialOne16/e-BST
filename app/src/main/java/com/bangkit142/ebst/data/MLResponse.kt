package com.bangkit142.ebst.data

import com.google.gson.annotations.SerializedName

data class MLResponse(

    @field:SerializedName("confidentLevel")
    val confidentLevel: String,

    @field:SerializedName("filename")
    val filename: String,

    @field:SerializedName("verdict")
    val verdict: String,

)

