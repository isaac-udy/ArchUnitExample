package com.isaacudy.archunit.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import dev.enro.core.compose.rememberNavigationContainer
import dev.enro.core.container.EmptyBehavior

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val exampleAnonymous = object : Thread() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            rememberNavigationContainer(
                root = RootScreen(),
                emptyBehavior = EmptyBehavior.CloseParent,
            ).Render()
        }
    }
}