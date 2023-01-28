package com.isaacudy.archunit.example.layers

import androidx.room.Database
import com.isaacudy.archunit.example.predicates.includingAssignableTypes
import com.isaacudy.archunit.example.predicates.includingEnclosingClasses
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass

object PersistenceLayer {
    val name = "persistence"

    val isEntity = describe<JavaClass>("is Entity") {
        it.simpleName.endsWith("Entity")
    }

    val isDao = describe<JavaClass>("is Dao") {
        it.simpleName.endsWith("Dao")
    }

    val isDatabase = describe<JavaClass>("is Database") {
        it.isAnnotatedWith(Database::class.java)
    }

    val isPersistenceLayer = isDao
        .or(isEntity)
        .or(isDatabase)
        .or(describe("related to Room") {
            it.annotations.any { it.rawType.name.startsWith("androidx.room") }
        })
        .includingEnclosingClasses()
        .includingAssignableTypes()
}