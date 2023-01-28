package com.isaacudy.archunit.example.predicates

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClass

/**
 * This predicate that roughly checks whether a class is a dagger or hilt related class,
 * this can be useful for ignoring generated code, as well as modules that are defined within the project itself
 */
val isDaggerOrHiltClass = describe<JavaClass>("is dagger or hilt class") { javaClass ->
    if (javaClass.name.contains("_Hilt")) return@describe true
    if (javaClass.simpleName.startsWith("Hilt_")) return@describe true
    if (javaClass.simpleName.startsWith("Dagger")) return@describe true
    if (javaClass.simpleName.contains("_Generated")) return@describe true
    if (javaClass.simpleName.contains("_ComponentTreeDeps")) return@describe true

    return@describe javaClass.annotations
        .map { it.rawType.name }
        .any {
            it.contains("GeneratedEntryPoint") ||
                    it.contains("DaggerGenerated") ||
                    it.contains("dagger.Module")
        }

}.includingEnclosingClasses()