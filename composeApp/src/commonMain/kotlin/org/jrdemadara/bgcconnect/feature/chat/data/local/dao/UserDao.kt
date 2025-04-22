package org.jrdemadara.bgcconnect.feature.chat.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jrdemadara.User
import org.jrdemadara.UserQueries

class UserDao(private val queries: UserQueries) {

    fun getAllUsers(): Flow<List<User>> =
        queries.selectAllUsers()
            .asFlow()
            .mapToList(Dispatchers.Default)

    fun getUserById(id: Long): Flow<User?> {
        return queries.selectUser(id)
            .asFlow()
            .map { it.executeAsOneOrNull() }
    }

    fun getUserName(id: Long): String {
        return queries.selectUserById(id).executeAsOne().fullName
    }

    fun getAvatar(id: Long): String? {
        return queries.selectUserById(id).executeAsOne().avatar
    }

    fun getOnlineStatus(id: Long): Flow<Boolean> =
        queries.selectIsOnline(id)
            .asFlow()
            .map { it.executeAsOne().toInt() == 1 }

    fun getlastSeen(id: Long): String {
        return queries.selectUserById(id).executeAsOne().lastSeen.toString()
    }

    fun insert(user: User) {
        queries.insertUser(
            id = user.id,
            firstname = user.firstname,
            lastname = user.lastname,
            avatar = user.avatar,
            isOnline = user.isOnline,
            lastSeen = user.lastSeen
        )
    }

    fun updateOnlineStatus(id: Long, isOnline: Boolean) {
        println("Update online status of: $id")
        queries.updateUserOnlineStatus(
            isOnline = if (isOnline) 1 else 0,
            id = id
        )
    }

    fun updateLastSeen(id: Long, lastSeen: String?) {
        println("Update last seen of: $id")
        queries.updateUserLastSeen(
            lastSeen = lastSeen,
            id = id
        )
    }

    suspend fun deleteUser(id: Long) {
        queries.deleteUserById(id)
    }
}