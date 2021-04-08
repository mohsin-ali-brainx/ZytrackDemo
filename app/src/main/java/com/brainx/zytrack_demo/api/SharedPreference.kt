package com.brainx.zytrack_demo.api


import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreference @Inject constructor(@ApplicationContext val context: Context) {

    enum class SpKeys(val key: String) {
        FILE_NAME("Demo"),
        IS_LOGIN("isLogin")
    }

    private val preferences =
        context.getSharedPreferences(SpKeys.FILE_NAME.key, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()


    fun clearPrefs() {
        editor.clear().apply()
    }

    var isLogin: Boolean
        get() = preferences.getBoolean(SpKeys.IS_LOGIN.key, false)
        set(isLogin) {
            editor.apply {
                putBoolean(SpKeys.IS_LOGIN.key, isLogin)
                apply()
            }
        }
}
