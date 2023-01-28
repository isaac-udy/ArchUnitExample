package com.isaacudy.archunit.example.data.user

import com.isaacudy.archunit.example.domain.user.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val currentUser = userDao.get(USER_ID).map {
        User(
            id = it.id,
            name = it.name
        )
    }.shareIn(scope, SharingStarted.Lazily, 1)

    private val currentUserAndFavorites = userDao.getUsersWithFavorites(USER_ID)
        .shareIn(scope, SharingStarted.Lazily, 1)

    init {
        scope.launch {
            userDao.insertOrIgnore(
                UserEntity(USER_ID, "Default User")
            )
        }
    }

    fun getCurrentUser(): Flow<User> {
        return currentUser
    }

    fun getUserWithFavorites(): Flow<UserWithFavoritesEntity> {
        return currentUserAndFavorites
    }

    fun addIntegerFavorite(
        id: String,
    ) {
        scope.launch {
            userDao.insertFavorite(
                UserFavoriteEntity(
                    favoriteId = id,
                    favoriteType = UserFavoriteEntity.FavoriteType.INTEGER,
                    userId = USER_ID
                )
            )
        }
    }

    fun removeIntegerFavorite(
        id: String,
    ) {
        scope.launch {
            userDao.deleteFavorite(
                UserFavoriteEntity(
                    favoriteId = id,
                    favoriteType = UserFavoriteEntity.FavoriteType.INTEGER,
                    userId = USER_ID
                )
            )
        }
    }

    suspend fun addRealFavorite(
        id: String,
    ) {
        userDao.insertFavorite(
            UserFavoriteEntity(
                favoriteId = id,
                favoriteType = UserFavoriteEntity.FavoriteType.REAL,
                userId = USER_ID
            )
        )
    }

    suspend fun removeRealFavorite(
        id: String,
    ) {
        userDao.deleteFavorite(
            UserFavoriteEntity(
                favoriteId = id,
                favoriteType = UserFavoriteEntity.FavoriteType.REAL,
                userId = USER_ID
            )
        )
    }

    companion object {
        private const val USER_ID = "active_user"
    }
}