package com.isaacudy.archunit.example.predicates

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass

fun DescribedPredicate<JavaClass>.includingAssignableTypes(): DescribedPredicate<JavaClass> =
    describe("$description (including assignable types)") {
        it.allClassesSelfIsAssignableTo
            .filter { it.fullName != "java.lang.Object" }
            .any { assignable -> test(assignable) }
    }