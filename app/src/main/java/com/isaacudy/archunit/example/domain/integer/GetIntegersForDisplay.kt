package com.isaacudy.archunit.example.domain.integer

import com.isaacudy.archunit.example.domain.favorites.GetFavorites
import com.isaacudy.archunit.example.domain.favorites.model.Favorite
import com.isaacudy.archunit.example.domain.integer.model.IntegerForDisplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetIntegersForDisplay @Inject constructor(
    private val integerRepository: IntegerRepository,
    private val getIntegerName: GetIntegerName,
    private val getFavorites: GetFavorites,
) {
    operator fun invoke(): Flow<List<IntegerForDisplay>> {
        return combine(
            integerRepository.getAllIntegers(),
            getFavorites()
        ) { integers, userFavorites ->
            val favorites = userFavorites
                .filterIsInstance<Favorite.Integer>()
                .map { it.id }
                .toSet()

            integers.map { integer ->
                IntegerForDisplay(
                    id = integer.id,
                    value = integer.value,
                    name = getIntegerName(integer),
                    isFavorite = favorites.contains(integer.id)
                )
            }
        }
    }
}

