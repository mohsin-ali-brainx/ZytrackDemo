package com.brainx.zytrack_demo.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.brainx.androidext.ext.toJson
import com.brainx.zytrack_demo.models.UserModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceDataStore @Inject constructor(@ApplicationContext val context: Context) {
    companion object {
        val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        val USER_KEY = stringPreferencesKey("userData")
        val CLIENT_KEY = stringPreferencesKey("client")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val UID_KEY = stringPreferencesKey("uid")

    }

   private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Zytrack_Demo")

    suspend fun isLogin(isLogin:Boolean) {
        context.dataStore.edit {
            it[IS_LOGIN_KEY] = isLogin
        }
    }

    suspend fun headers(token:String,client:String,uid:String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN_KEY] = token
            it[CLIENT_KEY] = client
            it[UID_KEY] = uid
        }
    }

    suspend fun userData(userModel: UserModel){
        context.dataStore.edit {
            context.dataStore.edit {
                it[USER_KEY] = userModel?.toJson()
            }
        }
    }

    // reading values can be done by 3 methods
    suspend fun isLogin():Boolean?{
        val preferences = context.dataStore.data.first()
        return preferences[IS_LOGIN_KEY] ?:false
    }

    val isUserLoggedIn: Flow<Boolean> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        val isLoggedIn = it[IS_LOGIN_KEY] ?: false
        isLoggedIn
    }

    val isLogin: Flow<Boolean>
        get() = context.dataStore.data.map {
            val isLoggedIn = it[IS_LOGIN_KEY] ?: false
            isLoggedIn
        }

    val userData: Flow<UserModel?>
        get() = context.dataStore.data.map {
            val user = Gson().fromJson(it[USER_KEY], UserModel::class.java)
            user
        }
}