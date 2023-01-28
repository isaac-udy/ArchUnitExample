package com.isaacudy.archunit.example.feature.integer.details

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.isaacudy.archunit.example.domain.integer.RelatedInteger
import com.isaacudy.archunit.example.infrastructure.onSuccess
import com.isaacudy.archunit.example.ui.component.ItemCard
import com.isaacudy.archunit.example.ui.component.Tag
import dev.enro.annotations.NavigationDestination
import dev.enro.core.NavigationKey
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntegerDetailsKey(
    val integerId: String
) : NavigationKey.SupportsPush

@Composable
@NavigationDestination(IntegerDetailsKey::class)
fun IntegerDetailsScreen() {
    val viewModel = viewModel<IntegerDetailsViewModel>()
    val state by viewModel.stateFlow.collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        state.details.onSuccess { integer ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${integer.value}",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onBackground,
                )

                IconButton(
                    onClick = { viewModel.onToggleFavorite() },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = when (integer.isFavorite) {
                            true -> Icons.Default.Favorite
                            false -> Icons.Default.FavoriteBorder
                        },
                        contentDescription = null,
                    )
                }
            }

            Text(
                text = "Name",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = integer.name,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
            )

            Text(
                text = "Tags",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                integer.tags.forEach {
                    Tag(
                        text = it::class.java.simpleName,
                    )
                }
            }

            Text(
                text = "Related",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(top = 16.dp)
            )

            integer.relatedIntegers.forEach {
                RelatedIntegerCard(
                    item = it,
                    onSelected = {
                        viewModel.onRelatedIntegerSelected(
                            relatedIntegerId = it.relatedId ?: return@RelatedIntegerCard
                        )
                    }
                )
            }
            if (integer.relatedIntegers.isEmpty()) {
                Text(
                    text = "No interesting related integers",
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            IntegerFactButton(
                onUserRequestedFact = viewModel::onUserRequestedFact
            )
        }
    }
}

@Composable
fun RelatedIntegerCard(
    item: RelatedInteger,
    onSelected: () -> Unit
) {
    ItemCard(
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable { onSelected() },
        startPrimaryContent = {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                text = item.relatedValue.toString(),
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.high)
            )
        },
        endContent = {
            Box(modifier = Modifier.padding(end = 8.dp)) {
                Tag(
                    text = when (item.relationship) {
                        RelatedInteger.Relationship.SQUARE_ROOT -> "Square root"
                        RelatedInteger.Relationship.SQUARE -> "Square"
                        RelatedInteger.Relationship.FACTOR -> "Factor"
                    }
                )
            }
        }
    )
}

@Composable
fun IntegerFactButton(
    onUserRequestedFact: suspend () -> String,
) {
    var activeFact by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxWidth(),
        onClick = {
            coroutineScope.launch {
                activeFact = runCatching { onUserRequestedFact() }
                    .onFailure { Log.e("IntegerFactButton", "Failed to get fact", it) }
                    .getOrNull()
            }
        }
    ) {
        Text("Request a fact")
    }

    if (activeFact != null) {
        AlertDialog(
            title = {
                Text("Fact")
            },
            text = {
               Text(activeFact.orEmpty())
            },
            onDismissRequest = { activeFact = null },
            confirmButton = {
                TextButton(onClick = { activeFact = null }) {
                    Text(text = "Close")
                }
            }
        )
    }
}