package com.isaacudy.archunit.example.domain.user

import com.isaacudy.archunit.example.data.user.UserRepository
import com.isaacudy.archunit.example.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentUser @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<User> = userRepository.getCurrentUser()
}