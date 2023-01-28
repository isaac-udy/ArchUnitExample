package com.isaacudy.archunit.example.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaacudy.archunit.example.domain.favorites.GetFavoritesForDisplay
import com.isaacudy.archunit.example.domain.favorites.model.FavoriteForDisplay
import com.isaacudy.archunit.example.domain.user.GetCurrentUser
import com.isaacudy.archunit.example.domain.user.model.User
import com.isaacudy.archunit.example.infrastructure.AsyncState
import com.isaacudy.archunit.example.infrastructure.asAsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getFavorites: GetFavoritesForDisplay,
    private val getCurrentUser: GetCurrentUser,
) : ViewModel() {

    private val mutableUserFlow: MutableStateFlow<AsyncState<User>> = MutableStateFlow(AsyncState.none())
    val userFlow: StateFlow<AsyncState<User>> = mutableUserFlow

    private val mutableFavoritesFlow: MutableStateFlow<AsyncState<List<FavoriteForDisplay>>> = MutableStateFlow(AsyncState.none())
    val favoritesFlow: StateFlow<AsyncState<List<FavoriteForDisplay>>> = mutableFavoritesFlow

    init {
        getCurrentUser()
            .asAsyncState()
            .onEach { mutableUserFlow.value = it }
            .launchIn(viewModelScope)

        getFavorites()
            .asAsyncState()
            .onEach { mutableFavoritesFlow.value = it }
            .launchIn(viewModelScope)
    }
}