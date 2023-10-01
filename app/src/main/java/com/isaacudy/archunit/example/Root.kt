package com.isaacudy.archunit.example

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.isaacudy.archunit.example.feature.integer.list.IntegerListKey
import com.isaacudy.archunit.example.feature.profile.ProfileKey
import com.isaacudy.archunit.example.feature.real.list.RealListKey
import com.isaacudy.archunit.example.ui.theme.ArchUnitExampleTheme
import dev.enro.annotations.NavigationDestination
import dev.enro.core.NavigationKey
import dev.enro.core.compose.container.rememberNavigationContainerGroup
import dev.enro.core.compose.rememberNavigationContainer
import dev.enro.core.container.EmptyBehavior
import kotlinx.parcelize.Parcelize

@Parcelize
class RootScreen : NavigationKey.SupportsPush

@OptIn(ExperimentalAnimationApi::class)
@NavigationDestination(RootScreen::class)
@Composable
fun RootDestination() {
    val integerTab = rememberNavigationContainer(
        root = IntegerListKey(),
        emptyBehavior = EmptyBehavior.CloseParent
    )
    val realTab = rememberNavigationContainer(
        root = RealListKey(),
        emptyBehavior = EmptyBehavior.Action {
            integerTab.setActive()
            true
        }
    )
    val profileTab = rememberNavigationContainer(
        root = ProfileKey(),
        emptyBehavior = EmptyBehavior.Action {
            integerTab.setActive()
            true
        }
    )
    val bottomNavigationGroup = rememberNavigationContainerGroup(
        integerTab,
        realTab,
        profileTab,
    )

    ArchUnitExampleTheme {
        Column {
            AnimatedContent(
                targetState = bottomNavigationGroup.activeContainer,
                modifier = Modifier.weight(1f),
            ) { container ->
                container.Render()
            }
            BottomNavigation {
                bottomNavigationGroup.containers.forEach { container ->
                    val isSelected = container == bottomNavigationGroup.activeContainer
                    BottomNavigationItem(
                        selected = isSelected,
                        onClick = { container.setActive() },
                        icon = {
                            when (container) {
                                integerTab -> Text("42")
                                realTab -> Text("3.14")
                                profileTab -> Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null
                                )
                                else -> return@BottomNavigationItem
                            }


                        },
                        label = {
                            val title = when (container) {
                                integerTab -> "Integers"
                                realTab -> "Reals"
                                profileTab -> "Profile"
                                else -> return@BottomNavigationItem
                            }
                            Text(title)
                        }
                    )
                }
            }
        }
    }
}