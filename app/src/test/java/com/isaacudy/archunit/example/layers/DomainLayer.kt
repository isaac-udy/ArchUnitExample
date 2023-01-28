package com.isaacudy.archunit.example.layers

import com.isaacudy.archunit.example.predicates.includingAssignableTypes
import com.isaacudy.archunit.example.predicates.includingEnclosingClasses
import com.isaacudy.archunit.example.predicates.isSynthetic
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier

object DomainLayer {
    val name = "domain"

    val isUsecase = describe<JavaClass>("is UseCase") {
        if(!it.packageName.startsWith("com.isaacudy.archunit.example.domain")) return@describe false

        it.methods
            .filter { method ->
                not(isSynthetic)
                    .and(modifier(JavaModifier.PUBLIC))
                    .test(method)
            }
            .size == 1
    }

    val isModel = describe<JavaClass>("is Model") {
        it.packageName.startsWith("com.isaacudy.archunit.example.domain") && !isUsecase.test(it)
    }

    val isDomainLayer = isUsecase.or(isModel)
        .includingEnclosingClasses()
        .includingAssignableTypes()
}