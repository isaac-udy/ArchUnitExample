package com.isaacudy.archunit.example.layers

import com.isaacudy.archunit.example.predicates.includingAssignableTypes
import com.isaacudy.archunit.example.predicates.includingEnclosingClasses
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass

object RepositoryLayer {
    val name = "repository"


    val isRepository = describe<JavaClass>("is Repository") {
        it.simpleName.endsWith("Repository")
    }

    val isRepositoryLayer = isRepository
        .includingEnclosingClasses()
        .includingAssignableTypes()
}