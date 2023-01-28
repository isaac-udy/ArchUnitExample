package com.isaacudy.archunit.example.infrastructure

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class StateViewModel<State : Any> : ViewModel() {

    protected abstract val initialState: State

    private val mutableStateFlow: MutableStateFlow<State> by lazy {
        MutableStateFlow(initialState)
    }

    val stateFlow: StateFlow<State> by lazy {
        mutableStateFlow
    }

    protected var state: State
        get() = mutableStateFlow.value
        set(value) {
            mutableStateFlow.value = value
        }
}