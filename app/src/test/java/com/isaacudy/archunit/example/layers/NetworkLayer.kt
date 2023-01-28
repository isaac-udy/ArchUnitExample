package com.isaacudy.archunit.example.layers

import com.isaacudy.archunit.example.predicates.includingAssignableTypes
import com.isaacudy.archunit.example.predicates.includingEnclosingClasses
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass

object NetworkLayer {
    val name = "network"


    val isApi = describe<JavaClass>("is Api") {
        it.simpleName.endsWith("Api")
    }

    val isRequest = describe<JavaClass>("is Request") {
        it.simpleName.endsWith("Request")
    }

    val isResponse = describe<JavaClass>("is Response") {
        it.simpleName.endsWith("Response")
    }

    val isNetworkLayer = isApi.or(isResponse).or(isRequest)
        .includingEnclosingClasses()
        .includingAssignableTypes()
}