package com.brainx.zytrack_demo.network.apiServices

import com.brainx.zytrack_demo.models.UserModel
import com.brainx.zytrack_demo.utils.ApiUrls
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ZytrackApiService {
    @POST(ApiUrls.SIGN_IN)
    suspend fun signIn(@Body body: UserModel): Response<UserModel>
}