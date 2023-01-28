package com.isaacudy.archunit.example.layers

import com.isaacudy.archunit.example.predicates.includingAssignableTypes
import com.isaacudy.archunit.example.predicates.includingEnclosingClasses
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass

object ViewModelLayer {
    val name = "viewmodel"


    val isViewModel = describe<JavaClass>("is ViewModel") {
        it.simpleName.endsWith("ViewModel")
    }

    val isState = describe<JavaClass>("is State") {
        it.simpleName.endsWith("State")
    }

    val isViewModelLayer = isViewModel.or(isState)
        .includingEnclosingClasses()
        .includingAssignableTypes()
}