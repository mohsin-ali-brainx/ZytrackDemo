package com.brainx.zytrack_demo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProjectsModel (
    @SerializedName("name")
    @Expose
    var projectName : String,
    @SerializedName("id")
    @Expose
    var projectId : Int,
    @SerializedName("end_client_id")
    @Expose
    var end_client_id : Int,
    @SerializedName("end_client_name")
    @Expose
    var end_client_name : String
): Serializable
