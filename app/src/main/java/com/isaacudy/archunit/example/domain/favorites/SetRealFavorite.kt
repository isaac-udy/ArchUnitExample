package com.isaacudy.archunit.example.domain.favorites

import com.isaacudy.archunit.example.data.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetRealFavorite @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        realId: String,
        isFavorite: Boolean
    ) {
        when(isFavorite) {
            true -> userRepository.addRealFavorite(realId)
            false -> userRepository.removeRealFavorite(realId)
        }
    }
}