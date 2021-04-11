package com.brainx.zytrack_demo.repository

import com.brainx.androidbase.ext.request
import com.brainx.androidbase.models.AppException
import com.brainx.androidbase.network.ResultState
import com.brainx.zytrack_demo.models.ApiResponse
import com.brainx.zytrack_demo.models.UserModel
import com.brainx.zytrack_demo.network.ZYTRACK_SERVICE
import com.brainx.zytrack_demo.utils.ZytrackConstant.ACCESS_TOKEN_HEADER_KEY
import com.brainx.zytrack_demo.utils.ZytrackConstant.CLIENT_HEADER_KEY
import com.brainx.zytrack_demo.utils.ZytrackConstant.UI_TOKEN_HEADER_KEY
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
) {
    suspend fun signIn(
        user: UserModel,
        responseResult: (UserModel?, Boolean,String?,String?,String?) -> Unit,
        error: (AppException) -> Unit
    ) {
            request({
                ZYTRACK_SERVICE.signIn(user)
            }, { result, headers ->
                when (result) {
                    is ResultState.Error -> error(result.error)
                    is ResultState.Success -> {
                        //Can handle Headers here
                        headers?.apply {
                            responseResult(
                                result.data,
                                true,
                                get(ACCESS_TOKEN_HEADER_KEY),
                                get(CLIENT_HEADER_KEY),
                                get(UI_TOKEN_HEADER_KEY)
                                )
                        }
                    }
                }
            })
    }

    suspend fun signOut(
        responseResult: (ApiResponse?, Boolean) -> Unit,
        error: (AppException) -> Unit
    ) {
        request({
            ZYTRACK_SERVICE.logout()
        },
            { result, headers ->
                when (result) {
                    is ResultState.Error ->  {
                        result.error.apply {
                            error(this)
                        }
                    }
                    is ResultState.Success ->{
                        //Can handle Headers here
                        responseResult(result.data,true)
                    }

                }

            })
    }
}
