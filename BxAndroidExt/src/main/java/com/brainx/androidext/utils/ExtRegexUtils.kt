package com.brainx.androidext.utils

object ExtRegexUtils {
    const val NON_EMPTY = "(.|\\s)*\\S(.|\\s)*"
    const val EMAIL_REGEX =
        "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)\$"
    const val PASSWORD_REGEX =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\^\$*.\\[\\]{}\\(\\)?\\-“!@#%&/,><\\’:;|_~`])\\S{6,99}\$"
}