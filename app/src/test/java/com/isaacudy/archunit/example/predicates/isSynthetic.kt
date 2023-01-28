package com.isaacudy.archunit.example.predicates

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.domain.JavaModifier


val isSynthetic = describe<JavaMethod>("is synthetic") {
    it.modifiers.contains(JavaModifier.SYNTHETIC)
}
