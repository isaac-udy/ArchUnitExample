package com.isaacudy.archunit.example.domain.favorites

import com.isaacudy.archunit.example.domain.favorites.model.FavoriteForDisplay
import com.isaacudy.archunit.example.domain.integer.GetIntegersForDisplay
import com.isaacudy.archunit.example.domain.real.GetRealsForDisplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFavoritesForDisplay @Inject constructor(
    private val getRealsForDisplay: GetRealsForDisplay,
    private val getIntegersForDisplay: GetIntegersForDisplay,
) {
    operator fun invoke(): Flow<List<FavoriteForDisplay>> {
        val integerFavorites = getIntegersForDisplay()
            .map { integers ->
                integers
                    .filter { it.isFavorite }
                    .map {
                        FavoriteForDisplay.Integer(
                            id = it.id,
                            value = it . value,
                            name = it.name,
                        )
                    }
            }

        val realFavorites = getRealsForDisplay()
            .map { reals ->
                reals
                    .filter { it.isFavorite }
                    .map {
                        FavoriteForDisplay.Real(
                            id = it.id,
                            value = it . value,
                            name = it.name,
                        )
                    }
            }

        return combine(
            integerFavorites,
            realFavorites,
        ) { integers, reals ->
            (integers + reals).sortedBy {
                when(it) {
                    is FavoriteForDisplay.Integer -> it.value.toBigDecimal()
                    is FavoriteForDisplay.Real -> it.value
                }
            }
        }
    }
}