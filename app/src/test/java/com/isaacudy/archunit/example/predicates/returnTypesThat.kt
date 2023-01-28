package com.isaacudy.archunit.example.predicates

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.importer.ClassFileImporter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

fun returnTypesThat(
    predicate: DescribedPredicate<JavaClass>
) = describe<JavaMethod>("return types that ${predicate.description}") {
    it.reflect()
        .let { method ->
            val kotlinFunction = method.kotlinFunction
            when {
                kotlinFunction != null -> kotlinFunction.returnType.javaType
                else -> method.genericReturnType
            }
        }
        .allTypeParameters()
        .toSet()
        .filter { it.name != "void" }
        .filter { it.name != "kotlin.Unit" }
        .mapNotNull { ClassFileImporter().importClass(it) }
        .all { visibleFromReturn -> predicate.test(visibleFromReturn) }
}

private fun Type.allTypeParameters(): List<Class<*>> {
    return when (this) {
        is Class<*> -> listOf(this)
        is WildcardType -> lowerBounds.flatMap { it.allTypeParameters() } + upperBounds.flatMap { it.allTypeParameters() }
        is ParameterizedType -> (actualTypeArguments.flatMap { it.allTypeParameters() } + rawType as? Class<*>)
        else -> listOf()
    }.filterNotNull()
}

