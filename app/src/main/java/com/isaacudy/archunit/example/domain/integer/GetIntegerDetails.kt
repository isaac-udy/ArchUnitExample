package com.isaacudy.archunit.example.domain.integer

import com.isaacudy.archunit.example.data.user.UserFavoriteEntity
import com.isaacudy.archunit.example.data.user.UserRepository
import com.isaacudy.archunit.example.domain.integer.model.IntegerDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GetIntegerDetails @Inject constructor(
    private val integerRepository: IntegerRepository,
    private val userRepository: UserRepository,
    private val getIntegerTags: GetIntegerTags,
    private val getIntegerName: GetIntegerName,
    private val getRelatedIntegers: GetRelatedIntegers,
) {
    operator fun invoke(id: String): Flow<IntegerDetails> {
        val integerFlow = integerRepository.getAllIntegers()
            .map { integers ->
                integers.first { integer -> integer.id == id }
            }

        val isFavoriteFlow = userRepository.getUserWithFavorites()
            .map { entity ->
                entity.favorites
                    .filter { it.favoriteType == UserFavoriteEntity.FavoriteType.INTEGER }
                    .any { it.favoriteId == id }
            }

        return combine(
            integerFlow,
            isFavoriteFlow,
            getRelatedIntegers(id),
        ) { integer, isFavorite, relatedIntegers ->
            IntegerDetails(
                id = integer.id,
                name = getIntegerName(integer),
                value = integer.value,
                isFavorite = isFavorite,
                tags = getIntegerTags(integer),
                relatedIntegers = relatedIntegers,
            )
        }
    }
}