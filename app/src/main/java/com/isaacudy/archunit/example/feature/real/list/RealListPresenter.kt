package com.isaacudy.archunit.example.feature.real.list

import androidx.lifecycle.viewModelScope
import com.isaacudy.archunit.example.domain.favorites.SetRealFavorite
import com.isaacudy.archunit.example.domain.real.GetRealsForDisplay
import com.isaacudy.archunit.example.domain.real.model.RealForDisplay
import com.isaacudy.archunit.example.infrastructure.AsyncState
import com.isaacudy.archunit.example.infrastructure.StateViewModel
import com.isaacudy.archunit.example.infrastructure.asAsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RealListState(
    val reals: AsyncState<List<RealForDisplay>>
)

@HiltViewModel
class RealListPresenter @Inject constructor(
    getRealsForDisplay: GetRealsForDisplay,
    private val setRealFavorite: SetRealFavorite,
) : StateViewModel<RealListState>() {

    override val initialState: RealListState = RealListState(
        reals = AsyncState.none()
    )

    init {
        getRealsForDisplay()
            .asAsyncState()
            .onEach { reals ->
                state = state.copy(
                    reals = reals
                )
            }
            .launchIn(viewModelScope)
    }

    fun onToggleFavorite(
        real: RealForDisplay
    ) {
        viewModelScope.launch {
            setRealFavorite(
                realId = real.id,
                isFavorite = !real.isFavorite
            )
        }
    }
}