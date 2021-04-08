package com.brainx.androidbase.network

import android.net.ParseException
import com.brainx.androidbase.models.AppException
import com.brainx.androidbase.network.model.Error
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException

object ExceptionHandle {
    const val error_name = "error"

    fun handleException(e: Throwable?): AppException {
        val ex: AppException
        e?.let {
            when (it) {
                is HttpException -> {
                    return convertErrorBody(it)
                }
                is JsonParseException, is JSONException, is ParseException, is MalformedJsonException -> {
                    ex = AppException(
                        Error.PARSE_ERROR,
                        e
                    )

                    return ex
                }
                is ConnectException -> {
                    ex = AppException(
                        Error.NETWORK_ERROR,
                        e
                    )
                    return ex
                }
                is javax.net.ssl.SSLException -> {
                    ex = AppException(
                        Error.SSL_ERROR,
                        e
                    )
                    return ex
                }
                is ConnectTimeoutException -> {
                    ex = AppException(
                        Error.TIMEOUT_ERROR,
                        e
                    )
                    return ex
                }
                is java.net.SocketTimeoutException -> {
                    ex = AppException(
                        Error.TIMEOUT_ERROR,
                        e
                    )
                    return ex
                }
                is java.net.UnknownHostException -> {
                    ex = AppException(
                        Error.INTERNET_UNAVAILABLE,
                        e
                    )
                    return ex
                }
                is AppException -> return it

                else -> {
                    ex = AppException(
                        Error.UNKNOWN,
                        e
                    )
                    return ex
                }
            }
        }
        ex = AppException(Error.UNKNOWN, e)

        return ex
    }


    private fun convertErrorBody(throwable: HttpException): AppException {

        var jobError = throwable.response()?.errorBody()?.string()
         return try {
            val obj = JSONObject(jobError)
             AppException(
                 throwable.code(),
                 obj.getString(error_name), obj.getString(error_name)
             )


        } catch (exception: Exception) {
            jobError = throwable.message().toString()
             AppException(
                 throwable.code(),
                 jobError, jobError
             )
        }
    }

}