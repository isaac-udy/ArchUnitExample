package com.isaacudy.archunit.example.domain.integer

import com.isaacudy.archunit.example.domain.integer.model.IntegerModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetIntegers @Inject constructor(
    private val integerRepository: IntegerRepository
) {
    operator fun invoke(): Flow<List<IntegerModel>> {
        return integerRepository.getAllIntegers()
    }
}

