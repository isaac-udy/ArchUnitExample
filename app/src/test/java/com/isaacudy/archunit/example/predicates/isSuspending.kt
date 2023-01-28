package com.isaacudy.archunit.example.predicates

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaMethod
import kotlin.reflect.jvm.kotlinFunction

val isSuspending = describe<JavaMethod>("is suspending") {
    it.reflect().kotlinFunction?.isSuspend == true
}