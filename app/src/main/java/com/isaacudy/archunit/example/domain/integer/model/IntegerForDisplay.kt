package com.isaacudy.archunit.example.domain.integer.model

import java.math.BigInteger

data class IntegerForDisplay(
    val id: String,
    val value: BigInteger,
    val name: String,
    val isFavorite: Boolean,
)