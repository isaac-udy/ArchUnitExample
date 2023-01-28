package com.isaacudy.archunit.example.domain.integer.model

sealed class IntegerTagState {
    object Zero : IntegerTagState()
    object Even : IntegerTagState()
    object Odd : IntegerTagState()
    object Prime : IntegerTagState()
    object Composite : IntegerTagState()
    object Square : IntegerTagState()
}