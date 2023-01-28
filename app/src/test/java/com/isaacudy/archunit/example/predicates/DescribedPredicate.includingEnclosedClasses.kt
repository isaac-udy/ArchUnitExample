package com.isaacudy.archunit.example.predicates

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass

fun DescribedPredicate<JavaClass>.includingEnclosingClasses(): DescribedPredicate<JavaClass> =
    describe("$description (including enclosing classes)") {
        var testing: JavaClass? = it
        while (testing != null) {
            val result = this.test(testing)
            if (result) return@describe true

            testing = testing.enclosingClass.orElse(null)
        }
        return@describe false
    }