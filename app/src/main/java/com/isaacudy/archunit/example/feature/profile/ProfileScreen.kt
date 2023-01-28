package com.isaacudy.archunit.example.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isaacudy.archunit.example.domain.favorites.model.FavoriteForDisplay
import com.isaacudy.archunit.example.infrastructure.AsyncState
import com.isaacudy.archunit.example.infrastructure.onSuccess
import com.isaacudy.archunit.example.ui.component.ItemCard
import dev.enro.annotations.NavigationDestination
import dev.enro.core.NavigationKey
import kotlinx.parcelize.Parcelize

@Parcelize
class ProfileKey : NavigationKey.SupportsPush

@NavigationDestination(ProfileKey::class)
@Composable
fun ProfileDestination() {
    val viewModel = viewModel<ProfileViewModel>()
    val user by viewModel.userFlow.collectAsState()
    val favorites by viewModel.favoritesFlow.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
    ) {
        item {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onBackground,
                )

                TextButton(
                    onClick = { /*TODO*/ },
                    enabled = user is AsyncState.Success
                ) {
                    Text("Edit")
                }
            }

            user.onSuccess {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onBackground,
                )
            }
        }


        item {
            Text(
                text = "Favorites",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground,
            )
        }
        favorites.onSuccess { favorites ->
            items(favorites) { favorite ->
                FavoriteItemCard(item = favorite)
            }
        }
    }
}


@Composable
private fun FavoriteItemCard(
    item: FavoriteForDisplay,
) {
    val value = when (item) {
        is FavoriteForDisplay.Integer -> item.value.toString()
        is FavoriteForDisplay.Real -> item.value.toString()
    }
    val name = when (item) {
        is FavoriteForDisplay.Integer -> item.name
        is FavoriteForDisplay.Real -> item.name
    }
    ItemCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        startPrimaryContent = {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = value,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.high)
            )
        },
        startSecondaryContent = {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = name,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
            )
        }
    )
}