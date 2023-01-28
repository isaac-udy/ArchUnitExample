package com.isaacudy.archunit.example.feature.integer.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isaacudy.archunit.example.infrastructure.onSuccess
import com.isaacudy.archunit.example.ui.component.ItemCard
import dev.enro.annotations.NavigationDestination
import dev.enro.core.NavigationKey
import kotlinx.parcelize.Parcelize

@Parcelize
class IntegerListKey : NavigationKey.SupportsPush

@Composable
@NavigationDestination(IntegerListKey::class)
fun IntegerListScreen() {
    val viewModel = viewModel<IntegerListViewModel>()
    val state by viewModel.stateFlow.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Integers",
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        state.integers.onSuccess { integers ->
            items(integers) { item ->
                IntegerItemCard(
                    item = item,
                    onItemSelected = viewModel::onItemSelected,
                    onToggleItemFavorite = viewModel::onToggleItemFavorite
                )
            }
        }
    }
}

@Composable
private fun IntegerItemCard(
    item: IntegerItemState,
    onItemSelected: (IntegerItemState) -> Unit,
    onToggleItemFavorite: (IntegerItemState) -> Unit
) {
    ItemCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemSelected(item) },
        startPrimaryContent = {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = item.value.toString(),
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.high)
            )
        },
        startSecondaryContent = {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = item.name,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
            )
        },
        endContent = {
            IconButton(
                onClick = { onToggleItemFavorite(item) },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = when (item.isFavorite) {
                        true -> Icons.Default.Favorite
                        false -> Icons.Default.FavoriteBorder
                    },
                    contentDescription = null,
                )
            }
        }
    )
}