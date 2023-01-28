package com.isaacudy.archunit.example.predicates

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.base.HasDescription
import com.tngtech.archunit.core.domain.properties.HasSourceCodeLocation
import com.tngtech.archunit.lang.ArchCondition

fun <T> DescribedPredicate<T>.asCondition(): ArchCondition<T> where T : HasDescription, T : HasSourceCodeLocation {
    return ArchCondition.from(this)
}