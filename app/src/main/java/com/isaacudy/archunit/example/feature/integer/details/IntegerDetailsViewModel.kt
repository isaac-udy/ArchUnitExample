package com.isaacudy.archunit.example.feature.integer.details

import androidx.lifecycle.viewModelScope
import com.isaacudy.archunit.example.data.integer.IntegerFactApi
import com.isaacudy.archunit.example.domain.favorites.SetIntegerFavorite
import com.isaacudy.archunit.example.domain.integer.GetIntegerDetails
import com.isaacudy.archunit.example.domain.integer.model.IntegerDetails
import com.isaacudy.archunit.example.infrastructure.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.enro.core.push
import dev.enro.viewmodel.navigationHandle
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IntegerDetailsState(
    val details: AsyncState<IntegerDetails>
)

@HiltViewModel
class IntegerDetailsViewModel @Inject constructor(
    private val getIntegerDetails: GetIntegerDetails,
    private val setIntegerFavorite: SetIntegerFavorite,
    private val integerFactApi: IntegerFactApi,
) : StateViewModel<IntegerDetailsState>() {

    private val navigation by navigationHandle<IntegerDetailsKey>()

    override val initialState: IntegerDetailsState = IntegerDetailsState(
        details = AsyncState.none()
    )

    init {
        getIntegerDetails(navigation.key.integerId)
            .asAsyncState()
            .onEach { detailsState ->
                state = state.copy(
                    details = detailsState
                )
            }
            .launchIn(viewModelScope)
    }

    suspend fun onUserRequestedFact(): String {
        val currentInteger = state.details
            .getOrThrow()
            .value
            .intValueExact()

        return integerFactApi.getFact(currentInteger).body()!!
    }

    fun onRelatedIntegerSelected(relatedIntegerId: String) {
        navigation.push(IntegerDetailsKey(relatedIntegerId))
    }

    fun onToggleFavorite() {
        val details = state.details.getOrNull() ?: return
        viewModelScope.launch {
            setIntegerFavorite(details.id, !details.isFavorite)
        }
    }
}