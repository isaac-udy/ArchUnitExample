package com.isaacudy.archunit.example.domain.real

import com.isaacudy.archunit.example.data.real.RealRepository
import com.isaacudy.archunit.example.domain.real.model.RealModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetReals @Inject constructor(
    private val realRepository: RealRepository
) {
    operator fun invoke(): Flow<List<RealModel>> = realRepository.getAllReals()
}