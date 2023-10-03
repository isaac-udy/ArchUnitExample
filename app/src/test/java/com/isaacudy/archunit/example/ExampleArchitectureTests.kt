package com.isaacudy.archunit.example

import androidx.lifecycle.ViewModel
import com.isaacudy.archunit.example.layers.DomainLayer
import com.isaacudy.archunit.example.layers.NetworkLayer
import com.isaacudy.archunit.example.layers.PersistenceLayer
import com.isaacudy.archunit.example.layers.RepositoryLayer
import com.isaacudy.archunit.example.layers.ViewModelLayer
import com.isaacudy.archunit.example.predicates.isDaggerOrHiltClass
import com.isaacudy.archunit.example.predicates.isRealKotlinClass
import com.isaacudy.archunit.example.utils.DemoDisplayFormat
import com.tngtech.archunit.base.DescribedPredicate.alwaysTrue
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures
import kotlinx.coroutines.flow.Flow
import org.junit.Test
import kotlin.reflect.jvm.kotlinFunction

class ExampleArchitectureTests {
    init {
        // region Configure simple logging format for the live demo
        DemoDisplayFormat.install()
        // endregion
    }

    // In a real project, we should load/unload the classes in the BeforeClass and AfterClass
    val classes by lazy {
        ClassFileImporter()
            .importPackages("com.isaacudy.archunit.example")
            // region Filter the imported classes to exclude Dagger/Hilt and compiler-generated kotlin classes
            .that(not(isDaggerOrHiltClass)) // filter out some generated code
            .that(isRealKotlinClass) // filter out classes created by the Kotlin compiler
        // endregion
    }

    @Test
    fun `verify ViewModels are defined correctly`() {
        ArchRuleDefinition.classes()
            .that()
            .areAssignableTo(ViewModel::class.java)
            .or()
            .haveSimpleNameEndingWith("ViewModel")
            .should()
            .haveSimpleNameEndingWith("ViewModel")
            .andShould()
            .beAssignableTo(ViewModel::class.java)
            .check(classes)
    }

    @Test
    fun `verify ViewModels are defined correctly (but different)`() {
        val mightBeViewModel = describe<JavaClass>("might be a ViewModel") { cls ->
            cls.name.endsWith("ViewModel") ||
                    cls.isAssignableTo(ViewModel::class.java)
        }

        val isValidViewModel = describe<JavaClass>("is valid ViewModel") { cls ->
            cls.name.endsWith("ViewModel") &&
                    cls.isAssignableTo(ViewModel::class.java)
        }

        ArchRuleDefinition.classes()
            .that(mightBeViewModel)
            .should(ArchCondition.from(isValidViewModel))
            .check(classes)
    }

    // this is a bit of a silly/pointless test, but it's useful to show Kotlin interop
    @Test
    fun `classes with companion objects should only define suspending functions (??)`() {
        val hasCompanion = describe<JavaClass>("has companion") { cls ->
            val kclass = cls.reflect().kotlin
            return@describe kclass.nestedClasses.any { it.isCompanion }
        }

        val isSuspending = describe<JavaMethod>("is suspending") {
            val kfun = it.reflect().kotlinFunction
                ?: return@describe false
            return@describe kfun.isSuspend
        }

        ArchRuleDefinition.methods()
            .that()
            .arePublic()
            .and()
            .areDeclaredInClassesThat(hasCompanion)
            .should(ArchCondition.from(isSuspending))
            .check(classes)
    }

    @Test
    fun `The ViewModel layer can depend on the Domain layer`() {
        Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer(PersistenceLayer.name).definedBy(PersistenceLayer.isPersistenceLayer)
            .layer(NetworkLayer.name).definedBy(NetworkLayer.isNetworkLayer)
            .layer(RepositoryLayer.name).definedBy(RepositoryLayer.isRepositoryLayer)
            .layer(DomainLayer.name).definedBy(DomainLayer.isDomainLayer)
            .layer(ViewModelLayer.name).definedBy(ViewModelLayer.isViewModelLayer)

            .whereLayer(ViewModelLayer.name).mayOnlyAccessLayers(DomainLayer.name)
            .check(classes)
    }

    @Test
    fun `The Repository layer can depend on the Network layer, Persistence layer and Models from the Domain layer`() {
        Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer(PersistenceLayer.name).definedBy(PersistenceLayer.isPersistenceLayer)
            .layer(NetworkLayer.name).definedBy(NetworkLayer.isNetworkLayer)
            .layer(RepositoryLayer.name).definedBy(RepositoryLayer.isRepositoryLayer)
            .layer(DomainLayer.name).definedBy(DomainLayer.isDomainLayer)
            .layer(ViewModelLayer.name).definedBy(ViewModelLayer.isViewModelLayer)

            .whereLayer(RepositoryLayer.name)
            .mayOnlyAccessLayers(NetworkLayer.name, PersistenceLayer.name)
            .ignoreDependency(alwaysTrue(), DomainLayer.isModel)
            .check(classes)
    }

    @Test
    fun `Repositories should live in the data package`() {
        ArchRuleDefinition.classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .resideInAPackage("com.isaacudy.archunit.example.data..")
            .check(classes)
    }

    @Test
    fun `Repository object functions must be suspending, or return Flows`() {
        ArchRuleDefinition.methods()
            .that(
                describe("declared in Repository") {
                    it.owner.simpleName.endsWith("Repository")
                }
            )
            .and()
            .arePublic()
            .and()
            .doNotHaveModifier(JavaModifier.SYNTHETIC)
            .should(
                ArchCondition.from(
                    describe("functions that are suspending") {
                        it.reflect().kotlinFunction?.isSuspend == true
                    }
                )
            )
            .orShould()
            .haveRawReturnType(assignableTo(Flow::class.java))
            .check(classes)
    }
}