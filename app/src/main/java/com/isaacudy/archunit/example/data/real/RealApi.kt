package com.isaacudy.archunit.example.data.real

import javax.inject.Inject
import javax.inject.Singleton

data class RealResponse(
    val value: String,
    val name: String,
)

@Singleton
class RealApi @Inject constructor() {
    @Suppress("RedundantSuspendModifier")
    suspend fun getReals(): List<RealResponse> {
        return listOf(
            RealResponse(
                value = "0.3333333333",
                name = "One-third"
            ),
            RealResponse(
                value = "1.6180339887",
                name = "Golden Ratio"
            ),
            RealResponse(
                value = "3.1415926535",
                name = "PI"
            ),
            RealResponse(
                value = "2.4142135623",
                name = "Silver Ratio"
            ),
            RealResponse(
                value = "3.3027756377",
                name = "Bronze Ratio"
            ),
            RealResponse(
                value = "3.3598856662",
                name = "Reciprocal fibonacci constant"
            ),
            RealResponse(
                value = "2.7182818284",
                name = "Euler's number"
            ),
        )
    }
}