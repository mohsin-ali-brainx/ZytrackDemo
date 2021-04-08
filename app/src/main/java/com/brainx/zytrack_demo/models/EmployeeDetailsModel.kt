package com.brainx.zytrack_demo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EmployeeDetailsModel(
    @SerializedName("language")
    @Expose
    var language : String = "en",
    @SerializedName("work_types")
    @Expose
    var work_type : Map<String,String>?,
    @Expose
    @SerializedName("projects") var projects : List<ProjectsModel>
): Serializable {
}
