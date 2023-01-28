package com.isaacudy.archunit.example.domain.real.model

import java.math.BigDecimal

data class RealForDisplay(
    val id: String,
    val value: BigDecimal,
    val name: String,
    val isFavorite: Boolean,
)