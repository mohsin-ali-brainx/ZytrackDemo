package com.brainx.androidbase.models

import com.brainx.androidbase.network.model.Error

class AppException : Exception {

    var errorMsg: String
    var errCode: Int = 0
    var errorLog: String?

    constructor(errCode: Int, error: String?, errorLog: String? = "") : super(error) {
        this.errorMsg = error ?: ""
        this.errCode = errCode
        this.errorLog = errorLog?:this.errorMsg
    }

    constructor(error: Error, e: Throwable?) {
        errCode = error.getKey()
        errorMsg = error.getValue()
        errorLog = e?.message
    }
}