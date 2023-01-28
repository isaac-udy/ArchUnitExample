package com.isaacudy.archunit.example.domain.integer

import com.isaacudy.archunit.example.domain.integer.model.IntegerModel
import com.isaacudy.archunit.example.domain.integer.model.IntegerTagState
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt


@Singleton
class GetIntegerTags @Inject constructor() {
    operator fun invoke(integer: IntegerModel): List<IntegerTagState> {
        return listOfNotNull(
            IntegerTagState.Zero.takeIf { integer.isZero() },
            IntegerTagState.Even.takeIf { integer.isEven() },
            IntegerTagState.Odd.takeIf { integer.isOdd() },
            IntegerTagState.Prime.takeIf { integer.isPrime() },
            IntegerTagState.Composite.takeIf { integer.isComposite() },
            IntegerTagState.Square.takeIf { integer.isSquare() },
        )
    }
}

private fun IntegerModel.isEven(): Boolean {
    return value.intValueExact() % 2 == 0
}

private fun IntegerModel.isOdd(): Boolean {
    return !isEven()
}

private fun IntegerModel.isZero(): Boolean {
    return value.intValueExact() == 0
}

private fun IntegerModel.isPrime(): Boolean {
    return value.isProbablePrime(16)
}

private fun IntegerModel.isComposite(): Boolean {
    return !isPrime() && !isZero()
}

private fun IntegerModel.isSquare(): Boolean {
    val root = sqrt(value.intValueExact().toDouble()).toInt()
    return (root * root) == value.intValueExact() && !isZero()
}