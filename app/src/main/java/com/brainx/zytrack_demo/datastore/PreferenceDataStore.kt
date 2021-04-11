package com.brainx.zytrack_demo.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import com.brainx.zytrack_demo.models.UserModel
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.utils.replaceBrackets
import com.brainx.zytrack_demo.utils.toJsonString
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
        const val PREFERENCE_NAME = "Zytrack_Demo"
        val IS_LOGIN_PREF_KEY = preferencesKey<Boolean>("isLogin")
        val USER_PREF_KEY = preferencesKey<String>("userData")
        val CLIENT_PREF_KEY = preferencesKey<String>("client")
        val ACCESS_TOKEN_PREF_KEY = preferencesKey<String>("access_token")
        val UID_PREF_KEY = preferencesKey<String>("uid")
    }

    private val datastore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend fun clearPreferenceDataStore(response: (Boolean) -> Unit) {
        datastore.edit {
            it.clear()
            response(true)
        }
    }

    suspend fun clearPreferenceDataStoreKey(prefKey: Preferences.Key<Any>) {
        datastore.edit {
            it.remove(prefKey)
        }
    }

    suspend fun isLogin(isLogin: Boolean, response: (Boolean) -> Unit) {
        datastore.edit {
            it[IS_LOGIN_PREF_KEY] = isLogin
            response(true)
        }
    }

    suspend fun headers(token: String, client: String, uid: String) {
        datastore.edit {
            it[ACCESS_TOKEN_PREF_KEY] = token
            it[CLIENT_PREF_KEY] = client
            it[UID_PREF_KEY] = uid
        }
    }

    suspend fun userData(userModel: String) {
        val userData = userModel?.replaceBrackets()
        userData?.let {
            datastore.edit { prefrence ->
                prefrence[USER_PREF_KEY] = it
            }
        }
    }

    // reading values can be done by 3 methods
    val header: Flow<Map<String, String>> = datastore.data.catch { exception ->
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

    suspend fun isLogin(): Boolean? {
        val preferences = datastore.data.first()
        return preferences[IS_LOGIN_PREF_KEY] ?: false
    }

    val isUserLoggedIn: Flow<Boolean> = datastore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        val isLoggedIn = it[IS_LOGIN_PREF_KEY] ?: false
        isLoggedIn
    }

    val isLogin: Flow<Boolean>
        get() = datastore.data.map {
            val isLoggedIn = it[IS_LOGIN_PREF_KEY] ?: false
            isLoggedIn
        }

    val userData: Flow<UserModel?>
        get() = datastore.data.map {
            val user = it[USER_PREF_KEY] ?: null
            val model = Gson().fromJson(user?.toJsonString(), UserModel::class.java)
            model
        }
}