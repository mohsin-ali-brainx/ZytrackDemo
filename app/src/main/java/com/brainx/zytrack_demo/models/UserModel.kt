package com.brainx.zytrack_demo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserModel(

    @SerializedName("id")
    @Expose
    var id: Int? = 0,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("uid")
    @Expose
    var uid: String? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("password")
    var password: String? = null,

    @SerializedName("active?")
    @Expose
    var active: Boolean? = false,

    @SerializedName("device_token")
    @Expose
    var device_token: String? = null,

    @SerializedName("app_platform")
    @Expose
    var app_platform: String? = null,

    @SerializedName("app_version")
    @Expose
    var app_version: String? = null,

    @SerializedName("employee_details")
    @Expose
    var employee_details: EmployeeDetailsModel? = null,
): Serializable