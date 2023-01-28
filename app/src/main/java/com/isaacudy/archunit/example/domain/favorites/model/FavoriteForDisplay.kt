package com.isaacudy.archunit.example.domain.favorites.model

import java.math.BigDecimal
import java.math.BigInteger

sealed class FavoriteForDisplay {
    data class Integer(
        val id: String,
        val value: BigInteger,
        val name: String,
    ) : FavoriteForDisplay()

    data class Real(
        val id: String,
        val value: BigDecimal,
        val name: String,
    ) : FavoriteForDisplay()
}