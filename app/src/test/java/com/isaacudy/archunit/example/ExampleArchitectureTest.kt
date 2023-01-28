package com.isaacudy.archunit.example

import com.isaacudy.archunit.example.layers.*
import com.isaacudy.archunit.example.predicates.*
import com.isaacudy.archunit.example.utils.DemoDisplayFormat
import com.tngtech.archunit.base.DescribedPredicate.anyElementThat
import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass.Predicates.*
import com.tngtech.archunit.core.domain.JavaMember.Predicates.declaredIn
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.name
import com.tngtech.archunit.core.domain.properties.HasReturnType.Predicates.rawReturnType
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Test

@Suppress("MemberVisibilityCanBePrivate", "HasPlatformType")
class ExampleArchitectureTest {

    init {
        DemoDisplayFormat.install()
    }

    // region Load classes
    val classes by lazy {
        ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS) // This doesn't actually work correctly for Android projects
            .withImportOption { location -> !location.contains("debugUnitTest") } // so instead we can do this to filter out our test classes
            .importPackages("com.isaacudy.archunit.example")
            .that(not(isDaggerOrHiltClass))
            .that(isRealKotlinClass)
    }
    // endregion

    // region Layer-based architecture definition
    val persistenceLayer = "Persistence Layer"
    val networkLayer = "Network Layer"
    val repositoryLayer = "Repository Layer"
    val domainLayer = "Domain Layer"
    val viewModelLayer = "ViewModel Layer"
    val unverified = "Unverified"

    val architecture = Architectures.layeredArchitecture()
        .consideringAllDependencies()
        .layer(persistenceLayer).definedBy(PersistenceLayer.isPersistenceLayer)
        .layer(networkLayer).definedBy(NetworkLayer.isNetworkLayer)
        .layer(repositoryLayer).definedBy(RepositoryLayer.isRepositoryLayer)
        .layer(domainLayer).definedBy(DomainLayer.isDomainLayer)
        .layer(viewModelLayer).definedBy(ViewModelLayer.isViewModelLayer)
        .layer(unverified).definedBy(
            not(PersistenceLayer.isPersistenceLayer)
                .and(not(NetworkLayer.isNetworkLayer))
                .and(not(RepositoryLayer.isRepositoryLayer))
                .and(not(DomainLayer.isDomainLayer))
                .and(not(ViewModelLayer.isViewModelLayer))
        )
    // endregion

    // region Persistence Layer
    /**
     * The Persistence layer should contain objects named "Dao" and "Entity"
     * The Persistence layer manages the storage and retrieval of locally persisted data
     *
     * Dao object functions must be suspending, or return Flows
     * Dao objects must not depend on other Dao objects
     *
     * Entity objects are used as return types from functions on Dao objects
     *
     * The Persistence layer should not depend on any other layer
     */
    @Test
    fun persistenceLayer_layerAccess() {
        architecture
            .whereLayer(persistenceLayer)
            .mayOnlyAccessLayers(unverified)
            .check(classes)
    }

    @Test
    fun persistenceLayer_daoObjectFunctionsMustBeSuspendingOrReturnFlows() {
        ArchRuleDefinition.methods()
            .that(declaredIn(PersistenceLayer.isDao))
            .and()
            .arePublic()
            .should(
                isSuspending.or(rawReturnType(assignableTo(Flow::class.java)))
                    .asCondition()
            )
            .`as`("functions declared in Daos must be suspending or return a Flow")
            .check(classes)
    }

    @Test
    fun persistenceLayer_daoObjectFunctionsMustReturnEntities() {
        ArchRuleDefinition.methods()
            .that(declaredIn(PersistenceLayer.isDao))
            .and()
            .arePublic()
            .should(
                returnTypesThat(
                    PersistenceLayer.isEntity
                        .or(isPrimitive)
                        .or(assignableTo(List::class.java))
                        .or(assignableTo(Flow::class.java))
                ).asCondition()
            )
            .`as`("functions declared in Daos that return a value must return Entities or primitives")
            .check(classes)
    }
    // endregion

    // region Network Layer
    /**
     * The Network layer should contain objects named "Api", "Request", and "Response"
     * The Network layer manages the storage and retrieval of data over the Network
     *
     * Api objects are the interface to the Network layer
     * Api object functions should be suspending
     * Api objects must not depend on other Api objects
     *
     * Request objects are used as parameters to functions on Api objects
     * Response objects are used as return types from functions on Api objects
     *
     * The Network layer should not depend on any other layer
     */
    @Test
    fun networkLayer_layerAccess() {
        architecture
            .whereLayer(networkLayer)
            .mayOnlyAccessLayers(unverified)
            .check(classes)
    }
    // endregion

    // region Repository Layer
    /**
     * The Repository layer is made up of objects named "Repository"
     * The Repository layer is a bridge between the Domain layer and the Network/Persistence layer
     *
     * Repository objects may depend on Api objects
     * Repository objects may depend on Dao objects
     * Repository objects must not depend on other Repository objects
     * Repository objects must return Model objects (which belong to the Domain layer)
     * Repository object functions must be suspending, or return Flows
     *
     * The Repository layer can depend on the Network layer, Persistence layer and models from the Domain layer
     */
    @Test
    fun repositoryLayer_layerAccess() {
        architecture
            .whereLayer(repositoryLayer)
            .mayOnlyAccessLayers(unverified, persistenceLayer, networkLayer)
            .ignoreDependency(RepositoryLayer.isRepositoryLayer, DomainLayer.isModel)
            .`as`("The Repository layer can depend on the Network layer, Persistence layer and models from the Domain layer")
            .because("The Repository layer is a bridge between the Domain layer and the Network/Persistence layer")
            .check(classes)
    }

    @Test
    fun repositoryLayer_repositoryObjectsMustNotDependOnOtherRepositoryObjects() {
        ArchRuleDefinition.constructors()
            .that(declaredIn(RepositoryLayer.isRepository))
            .should()
            .notHaveRawParameterTypes(anyElementThat(RepositoryLayer.isRepository))
            .`as`(
                """
                Repositories should not depend on other Repositories
            """.trimIndent()
            )
            .because(
                """
                we want to keep the Repository layer flat, and avoid recursive relationships at this layer. If a recursive relationship is required, the Domain layer is the correct place to describe this.
            """.trimIndent()
            )
            .check(classes)
    }

    @Test
    fun repositoryLayer_repositoryObjectsMustReturnModelObjects() {
        ArchRuleDefinition.methods()
            .that(declaredIn(RepositoryLayer.isRepository))
            .and()
            .arePublic()
            .and(not(isSynthetic))
            .should(
                returnTypesThat(
                    DomainLayer.isModel
                        .or(isPrimitive)
                        .or(assignableTo(List::class.java))
                        .or(assignableTo(Flow::class.java))
                ).asCondition()
            )
            .`as`("Repository objects must return Model objects (which belong to the Domain layer)")
            .check(classes)
    }

    @Test
    fun repositoryLayer_repositoryMethodsMustBeSuspendingOrReturnFlows() {
        ArchRuleDefinition.methods()
            .that(declaredIn(RepositoryLayer.isRepository))
            .and()
            .arePublic()
            .and(not(isSynthetic))
            .should(
                isSuspending
                    .or(rawReturnType(assignableTo(Flow::class.java)))
                    .asCondition()
            )
            .`as`("Repository methods must be suspending or return flows")
            .check(classes)
    }
    // endregion

    // region Domain Layer
    /**
     * The Domain layer is made up of objects named "Model" and "UseCase"
     *
     * UseCase objects must contain only one public function which returns Model objects
     * UseCase objects may depend on other UseCase objects
     * UseCase objects may depend on Repository objects
     * UseCase functions must be suspending, or return Flows
     *
     * Model objects should consist of data only, and not access UseCases or Repositories
     *
     * Unlike other layers, Model objects and UseCase objects are not required to be suffixed with "Model" or "UseCase" (although they may be)
     *
     * The Domain layer can depend on the Repository layer
     */
    @Test
    fun domainLayer_layerAccess() {
        architecture
            .whereLayer(domainLayer)
            .mayOnlyAccessLayers(unverified, repositoryLayer)
            .check(classes)
    }

    @Test
    fun domainLayer_useCaseMethodsMustReturnModels() {
        ArchRuleDefinition.methods()
            .that(declaredIn(DomainLayer.isUsecase))
            .and()
            .arePublic()
            .should(
                returnTypesThat(
                    DomainLayer.isModel
                        .or(isPrimitive)
                        .or(assignableTo(List::class.java))
                        .or(assignableTo(Flow::class.java))
                ).asCondition()
            )
            .`as`("UseCase methods must return Models")
            .check(classes)
    }

    @Test
    fun domainLayer_useCaseMethodsMustBeSuspendingOrReturnFlows() {
        ArchRuleDefinition.methods()
            .that(declaredIn(DomainLayer.isUsecase))
            .and()
            .arePublic()
            .should(
                isSuspending.or(rawReturnType(assignableTo(Flow::class.java)))
                    .asCondition()
            )
            .`as`("UseCase methods must be suspending or return a Flow")
            .check(classes)
    }

    @Test
    fun domainLayer_modelObjectsShouldNotAccessUseCasesOrRepositories() {
        ArchRuleDefinition.noClasses()
            .that(DomainLayer.isModel)
            .should()
            .accessClassesThat(
                DomainLayer.isUsecase
                    .or(RepositoryLayer.isRepositoryLayer)
                    .and(simpleNameContaining("LiveLiterals"))
            )
            .`as`("Model objects should consist of data only, and not access UseCases or Repositories")
            .check(classes)
    }
    // endregion

    // region ViewModel Layer
    /**
     * The ViewModel layer should contain objects named "ViewModel" and "State"
     * ViewModel objects should expose a single StateFlow containing a State
     * ViewModel objects should expose methods which return Unit, but no other types
     *
     * The ViewModel layer can depend on the Domain layer only
     */
    @Test
    fun viewModelLayer_layerAccess() {
        architecture
            .whereLayer(viewModelLayer)
            .mayOnlyAccessLayers(unverified, domainLayer)
            .`as`("The ViewModel layer can depend on the Domain layer only")
            .check(classes)
    }

    @Test
    fun viewModelLayer_viewModelsOnlyExposeSingleState() {
        ArchRuleDefinition.methods()
            .that(declaredIn(ViewModelLayer.isViewModel))
            .and()
            .arePublic()
            .and(not(isSynthetic))
            .should(
                returnTypesThat(type(Unit::class.java))
                    .asCondition()
            )
            .orShould(
                returnTypesThat(
                    ViewModelLayer.isState
                        .or(type(StateFlow::class.java))
                )
                    .and(rawReturnType(StateFlow::class.java))
                    .and(name("getStateFlow"))
                    .asCondition()
            )
            .`as`(
                """
                ViewModel objects should expose a single StateFlow containing a State
                ViewModel objects should expose methods which have no return type
            """.trimIndent()
            )
            .check(classes)
    }
    // endregion

    // region General architecture verification
    @Test
    fun unverifiedLayer_layerAccess() {
        architecture
            .whereLayer(unverified)
            .mayNotAccessAnyLayer()
            .`as`("Objects that don't belong to any layer should not access any of the real architectural layers")
            .because("objects in this layer should be considered to be utilities or general infrastructure that lives outside the architecture")
            .check(classes)
    }

    @Test
    fun verifyAllClassesExistInArchitecture() {
        architecture
            .ensureAllClassesAreContainedInArchitecture()
            .check(classes)
    }

    @Test
    fun verifyAllClassesExistInOnlyOneLayer() {
        classes
            .map { javaClass ->
                javaClass to listOfNotNull(
                    persistenceLayer.takeIf { PersistenceLayer.isPersistenceLayer.test(javaClass) },
                    networkLayer.takeIf { NetworkLayer.isNetworkLayer.test(javaClass) },
                    repositoryLayer.takeIf { RepositoryLayer.isRepository.test(javaClass) },
                    domainLayer.takeIf { DomainLayer.isDomainLayer.test(javaClass) },
                    viewModelLayer.takeIf { ViewModelLayer.isViewModelLayer.test(javaClass) },
                )
            }
            .filter { it.second.size > 1 }
            .joinToString("\n") { (javaClass, layers) ->
                "${javaClass.name} belongs [${layers.joinToString()}]"
            }
            .takeIf { it.isNotEmpty() }
            ?.let { throw RuntimeException(it) }
    }
    // endregion
}