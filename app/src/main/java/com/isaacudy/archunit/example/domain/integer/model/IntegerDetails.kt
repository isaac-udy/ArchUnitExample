package com.isaacudy.archunit.example.domain.integer.model

import com.isaacudy.archunit.example.domain.integer.RelatedInteger
import java.math.BigInteger

data class IntegerDetails(
    val id: String,
    val name: String,
    val value: BigInteger,
    val tags: List<IntegerTagState>,
    val isFavorite: Boolean,
    val relatedIntegers: List<RelatedInteger>
)