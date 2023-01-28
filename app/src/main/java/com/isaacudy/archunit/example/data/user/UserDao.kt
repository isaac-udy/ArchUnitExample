package com.isaacudy.archunit.example.data.user

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Entity
data class UserFavoriteEntity(
    @PrimaryKey val favoriteId: String,
    val favoriteType: FavoriteType,
    val userId: String,
) {
    enum class FavoriteType {
        INTEGER,
        REAL,
    }
}

data class UserWithFavoritesEntity(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val favorites: List<UserFavoriteEntity>
)

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity WHERE id = :id LIMIT 1")
    fun get(id: String): Flow<UserEntity>

    @Transaction
    @Query("SELECT * FROM UserEntity WHERE id = :id LIMIT 1")
    fun getUsersWithFavorites(id: String): Flow<UserWithFavoritesEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: UserFavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: UserFavoriteEntity)
}