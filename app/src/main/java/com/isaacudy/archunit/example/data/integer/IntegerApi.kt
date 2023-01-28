package com.isaacudy.archunit.example.data.integer

import javax.inject.Inject
import javax.inject.Singleton

data class IntegerResponse(
    val value: String,
)

@Singleton
class IntegerApi @Inject constructor() {
    @Suppress("RedundantSuspendModifier")
    suspend fun getIntegers(): List<IntegerResponse> {
        return (0..100).map { IntegerResponse(it.toString()) }
    }
}