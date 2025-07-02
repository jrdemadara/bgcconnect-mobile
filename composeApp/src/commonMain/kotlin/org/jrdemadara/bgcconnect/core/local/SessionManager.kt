package org.jrdemadara.bgcconnect.core.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.toSuspendSettings
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.bgcconnect.feature.login.data.LoginDto

class SessionManager(private val settings:  Settings) {


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
        settings.putString(KEY_TOKEN, token)
        settings.putInt(KEY_USER_ID, data.id)
        settings.putString(KEY_PHONE, data.phone)
        settings.putString(KEY_CODE, data.code)
        settings.putInt(KEY_POINTS, data.points)
        settings.putInt(KEY_LEVEL, data.level)
        settings.putString(KEY_FIRSTNAME, data.firstname)
        settings.putString(KEY_LASTNAME, data.lastname)
        settings.putString(KEY_MIDDLENAME, data.middlename)
        settings.putString(KEY_EXTENSION, data.extension ?: "")
        settings.putString(KEY_AVATAR, data.avatar ?: "")
    }

    fun saveFCMToken(fcmToken: String) {
        settings.putString(KEY_FCM_TOKEN, fcmToken)
    }

    fun clearSession() {
        settings.clear()
    }

    fun isLoggedIn(): Boolean {
        return settings.hasKey(KEY_TOKEN)
    }

    fun getToken(): String? = settings.getStringOrNull(KEY_TOKEN)
    fun getFCMToken(): String? = settings.getStringOrNull(KEY_FCM_TOKEN)
    fun getUserId(): Int = settings.getInt(KEY_USER_ID, -1)
    @OptIn(ExperimentalSettingsApi::class)
    fun observeUserId(): Int = settings.getInt(KEY_USER_ID, -1)
    fun getCode(): String = settings.getString(KEY_CODE, "")
    fun getPhone(): String = settings.getString(KEY_PHONE, "")
    fun getPoints(): Int = settings.getInt(KEY_POINTS, -1)

    fun getFirstName(): String = settings.getString(KEY_FIRSTNAME, "")

    fun getFullName(): String {
        return "${settings.getString(KEY_FIRSTNAME, "")} ${settings.getString(KEY_LASTNAME, "")}".trim()
    }

    fun getAvatarUrl(): String? = settings.getStringOrNull(KEY_AVATAR)


}