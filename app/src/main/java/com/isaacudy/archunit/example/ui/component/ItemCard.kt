package com.isaacudy.archunit.example.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    startPrimaryContent: @Composable () -> Unit = {},
    startSecondaryContent: @Composable () -> Unit = {},
    endContent: @Composable () -> Unit = {},
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Box {
                    startPrimaryContent()
                }
                Box {
                    startSecondaryContent()
                }
            }
            Box {
                endContent()
            }
        }
    }
}