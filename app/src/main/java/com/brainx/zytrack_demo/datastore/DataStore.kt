package com.brainx.zytrack_demo.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.brainx.androidext.ext.toJson
import com.brainx.zytrack_demo.EmployeeDetails
import com.brainx.zytrack_demo.User
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
class DataStore @Inject constructor(@ApplicationContext val context: Context) {

    companion object {
        const val PREFERENCE_NAME = "Zytrack_Demo"
        private const val USER_PREFERENCES_NAME  = "user_preference"
        private const val DATA_STORE_FILE_NAME = "zytrack.pb"
        val IS_LOGIN_PREF_KEY = booleanPreferencesKey("isLogin")
        val USER_PREF_KEY = stringPreferencesKey("userData")
        val CLIENT_PREF_KEY = stringPreferencesKey("client")
        val ACCESS_TOKEN_PREF_KEY = stringPreferencesKey("access_token")
        val UID_PREF_KEY = stringPreferencesKey("uid")

    }

    // creating preference datastore
    val Context.preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)
    // creating user proto datastore
    private val Context.userPreferencesDataStore: DataStore<User> by dataStore(fileName = DATA_STORE_FILE_NAME, serializer = UserSerializer)

    // clearing complete preference datastore
    suspend fun clearPreferenceDataStore(response: (Boolean) -> Unit) {
        context.preferenceDataStore.edit {
            it.clear()
            response(true)
        }
        context.userPreferencesDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }

    // clearing a single preference using key
    suspend fun clearPreferenceDataStoreKey(prefKey: Preferences.Key<Any>) {
        context.preferenceDataStore.edit {
            it.remove(prefKey)
        }
    }

    suspend fun isLogin(isLogin: Boolean) {
        context.preferenceDataStore.edit {
            it[IS_LOGIN_PREF_KEY] = isLogin
        }
    }

    val isLogin:Flow<Boolean> = context.preferenceDataStore.data.catch { exception ->
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
        context.preferenceDataStore.edit {
            it[ACCESS_TOKEN_PREF_KEY] = token
            it[CLIENT_PREF_KEY] = client
            it[UID_PREF_KEY] = uid
        }
    }

    val header: Flow<Map<String, String>> = context.preferenceDataStore.data.catch { exception ->
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

    suspend fun userData(userModel: UserModel?) {
        val userData = userModel?.toJson()?.replaceBrackets()
        userData?.let {
            context.preferenceDataStore.edit { prefrence ->
                prefrence[USER_PREF_KEY] = it
            }
        }
    }

    val userData: Flow<UserModel?> = context.preferenceDataStore.data.catch { exception ->
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

    suspend fun user(userModel: UserModel?) {
        userModel?.let {userDataModel->
            var employeeDetails : EmployeeDetails.Builder = EmployeeDetails.newBuilder()
            employeeDetails?.apply {
                userDataModel.employee_details?.apply {
                    setLanguage(language)
                    putAllWorkType(work_type)
                }
            }
            context.userPreferencesDataStore.updateData { preference->
                val user = preference.toBuilder()
                user.setEmployeeDetails(employeeDetails).build()
                user.setName(userDataModel.name).build()
                user.setUid(userDataModel.uid).build()
            }
        }
    }

    val user : Flow<User> = context.userPreferencesDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(User.getDefaultInstance())
        } else {
            throw exception
        }
    }

}