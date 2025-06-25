package org.jrdemadara.bgcconnect.core.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.toSuspendSettings
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.bgcconnect.feature.login.data.LoginDto

class SessionManager(private val observableSettings:  ObservableSettings) {


    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "id"
        private const val KEY_PHONE = "phone"
        private const val KEY_CODE = "code"
        private const val KEY_POINTS = "points"
        private const val KEY_LEVEL = "level"
        private const val KEY_FIRSTNAME = "firstname"
        private const val KEY_LASTNAME = "lastname"
        private const val KEY_MIDDLENAME = "middlename"
        private const val KEY_EXTENSION = "extension"
        private const val KEY_AVATAR = "avatar"

        private const val KEY_FCM_TOKEN = "fcm_token"
    }

    fun saveSession(token: String, data: LoginDto) {
        observableSettings.putString(KEY_TOKEN, token)
        observableSettings.putInt(KEY_USER_ID, data.id)
        observableSettings.putString(KEY_PHONE, data.phone)
        observableSettings.putString(KEY_CODE, data.code)
        observableSettings.putInt(KEY_POINTS, data.points)
        observableSettings.putInt(KEY_LEVEL, data.level)
        observableSettings.putString(KEY_FIRSTNAME, data.firstname)
        observableSettings.putString(KEY_LASTNAME, data.lastname)
        observableSettings.putString(KEY_MIDDLENAME, data.middlename)
        observableSettings.putString(KEY_EXTENSION, data.extension ?: "")
        observableSettings.putString(KEY_AVATAR, data.avatar ?: "")
    }

    fun saveFCMToken(fcmToken: String) {
        observableSettings.putString(KEY_FCM_TOKEN, fcmToken)
    }

    fun clearSession() {
        observableSettings.clear()
    }

    fun isLoggedIn(): Boolean {
        return observableSettings.hasKey(KEY_TOKEN)
    }

    fun getToken(): String? = observableSettings.getStringOrNull(KEY_TOKEN)
    fun getFCMToken(): String? = observableSettings.getStringOrNull(KEY_FCM_TOKEN)
    fun getUserId(): Int = observableSettings.getInt(KEY_USER_ID, -1)
    @OptIn(ExperimentalSettingsApi::class)
    fun observeUserId(): Flow<Int> = observableSettings.getIntFlow(KEY_USER_ID, -1)
    fun getCode(): String = observableSettings.getString(KEY_CODE, "")
    fun getPhone(): String = observableSettings.getString(KEY_PHONE, "")
    fun getPoints(): Int = observableSettings.getInt(KEY_POINTS, -1)

    fun getFirstName(): String = observableSettings.getString(KEY_FIRSTNAME, "")

    fun getFullName(): String {
        return "${observableSettings.getString(KEY_FIRSTNAME, "")} ${observableSettings.getString(KEY_LASTNAME, "")}".trim()
    }

    fun getAvatarUrl(): String? = observableSettings.getStringOrNull(KEY_AVATAR)


}