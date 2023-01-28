package com.isaacudy.archunit.example.domain.favorites

import com.isaacudy.archunit.example.data.user.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetIntegerFavorite @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        integerId: String,
        isFavorite: Boolean
    ) {
        when(isFavorite) {
            true -> userRepository.addIntegerFavorite(integerId)
            false -> userRepository.removeIntegerFavorite(integerId)
        }
    }
}