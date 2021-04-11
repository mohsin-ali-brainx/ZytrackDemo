package com.brainx.zytrack_demo.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.brainx.zytrack_demo.models.UserModel
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.utils.replaceBrackets
import com.brainx.zytrack_demo.utils.toJsonString
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceDataStore @Inject constructor(@ApplicationContext val context: Context) {

    companion object {
        const val PREFERENCE_NAME = "Zytrack_Demo"
        val IS_LOGIN_PREF_KEY = booleanPreferencesKey("isLogin")
        val USER_PREF_KEY = stringPreferencesKey("userData")
        val CLIENT_PREF_KEY = stringPreferencesKey("client")
        val ACCESS_TOKEN_PREF_KEY = stringPreferencesKey("access_token")
        val UID_PREF_KEY = stringPreferencesKey("uid")

    }

    // creating datastore
    val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

    // clearing complete preference datastore
    suspend fun clearPreferenceDataStore(response: (Boolean) -> Unit) {
        context.datastore.edit {
            it.clear()
            response(true)
        }
    }

    // clearing a single preference using key
    suspend fun clearPreferenceDataStoreKey(prefKey: Preferences.Key<Any>) {
        context.datastore.edit {
            it.remove(prefKey)
        }
    }

    suspend fun isLogin(isLogin: Boolean) {
        context.datastore.edit {
            it[IS_LOGIN_PREF_KEY] = isLogin
        }
    }

    val isLogin:Flow<Boolean> = context.datastore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        val isLoggedIn = it[IS_LOGIN_PREF_KEY] ?: false
        isLoggedIn
    }

    suspend fun headers(token: String, client: String, uid: String) {
        context.datastore.edit {
            it[ACCESS_TOKEN_PREF_KEY] = token
            it[CLIENT_PREF_KEY] = client
            it[UID_PREF_KEY] = uid
        }
    }

    val header: Flow<Map<String, String>> = context.datastore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preference ->
        val headerMap: HashMap<String, String> = hashMapOf()
        ZytrackConstant.apply {
            headerMap[ACCESS_TOKEN_KEY] = preference[ACCESS_TOKEN_PREF_KEY] ?: ""
            headerMap[UID_KEY] = preference[UID_PREF_KEY] ?: ""
            headerMap[CLIENT_KEY] = preference[CLIENT_PREF_KEY] ?: ""
        }
        headerMap.filter {
            it.value.isNotEmpty()
        } as HashMap<String, String>
    }

    suspend fun userData(userModel: String) {
        val userData = userModel?.replaceBrackets()
        userData?.let {
            context.datastore.edit { prefrence ->
                prefrence[USER_PREF_KEY] = it
            }
        }
    }

    val userData: Flow<UserModel?> = context.datastore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        val user = it[USER_PREF_KEY] ?: null
        val model = Gson().fromJson(user?.toJsonString(), UserModel::class.java)
        model
    }

}