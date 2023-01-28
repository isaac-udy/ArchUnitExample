package com.isaacudy.archunit.example.predicates

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass

val isPrimitive = describe<JavaClass>("is primitive") {
    it.isPrimitive || it.fullName == "java.lang.String"
}