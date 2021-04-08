package com.brainx.zytrack_demo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("message")
    @Expose
    var message: String? = null
)