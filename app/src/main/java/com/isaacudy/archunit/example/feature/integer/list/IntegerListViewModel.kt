package com.isaacudy.archunit.example.feature.integer.list

import androidx.lifecycle.viewModelScope
import com.isaacudy.archunit.example.data.user.UserFavoriteEntity
import com.isaacudy.archunit.example.data.user.UserRepository
import com.isaacudy.archunit.example.domain.integer.GetIntegerName
import com.isaacudy.archunit.example.domain.integer.GetIntegers
import com.isaacudy.archunit.example.feature.integer.details.IntegerDetailsKey
import com.isaacudy.archunit.example.infrastructure.AsyncState
import com.isaacudy.archunit.example.infrastructure.StateViewModel
import com.isaacudy.archunit.example.infrastructure.asAsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.enro.core.push
import dev.enro.viewmodel.navigationHandle
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

data class IntegerListState(
    val integers: AsyncState<List<IntegerItemState>>
)

data class IntegerItemState(
    val id: String,
    val value: BigInteger,
    val isFavorite: Boolean,
    val name: String,
)

@HiltViewModel
class IntegerListViewModel @Inject constructor(
    getAllIntegers: GetIntegers,
    private val userRepository: UserRepository,
    private val getIntegerName: GetIntegerName,
) : StateViewModel<IntegerListState>() {

    private val navigation by navigationHandle<IntegerListKey>()

    override val initialState: IntegerListState = IntegerListState(
        integers = AsyncState.none()
    )

    init {
        combine(
            getAllIntegers(),
            userRepository.getUserWithFavorites(),
        ) { integers, userAndFavorites ->
            val favorites = userAndFavorites.favorites
                .filter { it.favoriteType == UserFavoriteEntity.FavoriteType.INTEGER }
                .map { it.favoriteId }
                .toSet()

            integers.map { model ->
                IntegerItemState(
                    id = model.id,
                    value = model.value,
                    isFavorite = favorites.contains(model.id),
                    name = getIntegerName(model)
                )
            }
        }
            .asAsyncState()
            .onEach { integerState ->
                state = state.copy(
                    integers = integerState
                )
            }
            .launchIn(viewModelScope)
    }

    fun onItemSelected(
        item: IntegerItemState
    ) {
        navigation.push(
            IntegerDetailsKey(integerId = item.id)
        )
    }

    fun onToggleItemFavorite(
        item: IntegerItemState
    ) {
        viewModelScope.launch {
            when (item.isFavorite) {
                false -> userRepository.addIntegerFavorite(item.id)
                true -> userRepository.removeIntegerFavorite(item.id)
            }
        }
    }
}