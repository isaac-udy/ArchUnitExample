package com.isaacudy.archunit.example

import com.isaacudy.archunit.example.predicates.isDaggerOrHiltClass
import com.isaacudy.archunit.example.predicates.isRealKotlinClass
import com.isaacudy.archunit.example.utils.DemoDisplayFormat
import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.importer.ClassFileImporter
import org.junit.Test

class LiveDemoTest {

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
    fun `The ViewModel layer can depend on the Domain layer`() {

        /*
        Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer(PersistenceLayer.name).definedBy(PersistenceLayer.isPersistenceLayer)
            .layer(NetworkLayer.name).definedBy(NetworkLayer.isNetworkLayer)
            .layer(RepositoryLayer.name).definedBy(RepositoryLayer.isRepositoryLayer)
            .layer(DomainLayer.name).definedBy(DomainLayer.isDomainLayer)
            .layer(ViewModelLayer.name).definedBy(ViewModelLayer.isViewModelLayer)

            .whereLayer(ViewModelLayer.name).mayOnlyAccessLayers(DomainLayer.name)
            .check(classes)
         */
    }

    @Test
    fun `The Repository layer can depend on the Network layer, Persistence layer and Models from the Domain layer`() {

        /*
        Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer(PersistenceLayer.name).definedBy(PersistenceLayer.isPersistenceLayer)
            .layer(NetworkLayer.name).definedBy(NetworkLayer.isNetworkLayer)
            .layer(RepositoryLayer.name).definedBy(RepositoryLayer.isRepositoryLayer)
            .layer(DomainLayer.name).definedBy(DomainLayer.isDomainLayer)
            .layer(ViewModelLayer.name).definedBy(ViewModelLayer.isViewModelLayer)

            .whereLayer(RepositoryLayer.name).mayOnlyAccessLayers(NetworkLayer.name, PersistenceLayer.name)
            .ignoreDependency(alwaysTrue(), DomainLayer.isModel)
            .check(classes)
         */
    }

    @Test
    fun `Repositories should live in the data package`() {


        /*
        ArchRuleDefinition.classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .resideInAPackage("com.isaacudy.archunit.example.data..")
            .check(classes)
         */
    }

    @Test
    fun `Repository object functions must be suspending, or return Flows`() {

        /*
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
         */
    }
}