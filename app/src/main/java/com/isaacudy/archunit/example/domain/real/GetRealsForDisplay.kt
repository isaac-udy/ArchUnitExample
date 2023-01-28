package com.isaacudy.archunit.example.domain.real

import com.isaacudy.archunit.example.domain.favorites.GetFavorites
import com.isaacudy.archunit.example.domain.favorites.model.Favorite
import com.isaacudy.archunit.example.domain.real.model.RealForDisplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRealsForDisplay @Inject constructor(
    private val getReals: GetReals,
    private val getFavorites: GetFavorites,
) {
    operator fun invoke(): Flow<List<RealForDisplay>> {
        return combine(
            getReals(),
            getFavorites()
        ) { reals, userFavorites ->
            val favorites = userFavorites
                .filterIsInstance<Favorite.Real>()
                .map { it.id }
                .toSet()

            reals.map { real ->
                RealForDisplay(
                    id = real.id,
                    value = real.value,
                    name = real.name,
                    isFavorite = favorites.contains(real.id)
                )
            }
        }
    }
}

