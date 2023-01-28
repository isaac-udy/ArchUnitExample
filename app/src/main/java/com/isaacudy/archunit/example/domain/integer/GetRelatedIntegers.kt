package com.isaacudy.archunit.example.domain.integer

import com.isaacudy.archunit.example.domain.integer.model.IntegerModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

data class RelatedInteger(
    val origin: IntegerModel,
    val relatedId: String?,
    val relatedValue: BigInteger,
    val relationship: Relationship
) {
    enum class Relationship {
        SQUARE_ROOT,
        SQUARE,
        FACTOR,
    }
}

@Singleton
class GetRelatedIntegers @Inject constructor(
    private val integerRepository: IntegerRepository
) {

    operator fun invoke(integerId: String) : Flow<List<RelatedInteger>> {
        return integerRepository.getAllIntegers()
            .map {  integers ->
                val integerIds = integers.associateBy { it.value }
                val selectedInteger = integers.first { it.id == integerId}
                val square = (selectedInteger.value.intValueExact() * selectedInteger.value.intValueExact()).toBigInteger()
                val factors = selectedInteger.value.getFactors()

                val relations =  (factors + square)
                    .toSet()
                    .filter { it != BigInteger.ONE && it != selectedInteger.value }

                return@map relations
                    .map {
                        RelatedInteger(
                            origin = selectedInteger,
                            relatedValue = it,
                            relatedId = integerIds[it]?.id,
                            relationship = when {
                                it > selectedInteger.value -> RelatedInteger.Relationship.SQUARE
                                it * it == selectedInteger.value -> RelatedInteger.Relationship.SQUARE_ROOT
                                else -> RelatedInteger.Relationship.FACTOR
                            }
                        )
                    }
                    .sortedBy { it.relatedValue }
            }
    }
}

// https://www.baeldung.com/java-list-factors-integer
private fun BigInteger.getFactors(): List<BigInteger> {
    val factors = mutableListOf<Int>()
    val n = intValueExact()
    var i = 1
    while (i <= sqrt(n.toDouble())) {
        if (n % i == 0) {
            factors.add(i)
            factors.add(n / i)
        }
        i++
    }
    return factors.map {
        it.toBigInteger()
    }
}