package com.isaacudy.archunit.example.domain.favorites

import com.isaacudy.archunit.example.data.user.UserFavoriteEntity
import com.isaacudy.archunit.example.data.user.UserRepository
import com.isaacudy.archunit.example.domain.favorites.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFavorites @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<Favorite>> {
        return userRepository.getUserWithFavorites()
            .map { entity ->
                entity.favorites.map {
                    when(it.favoriteType) {
                        UserFavoriteEntity.FavoriteType.INTEGER -> Favorite.Integer(it.favoriteId)
                        UserFavoriteEntity.FavoriteType.REAL -> Favorite.Real(it.favoriteId)
                    }
                }
            }
    }
}