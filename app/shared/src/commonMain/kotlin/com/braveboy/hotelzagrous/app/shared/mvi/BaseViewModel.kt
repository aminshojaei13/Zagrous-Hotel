package com.braveboy.hotelzagrous.app.shared.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State, Intent>(initialState: State) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    abstract fun onIntent(intent: Intent)

    protected fun updateState(reducer: (State) -> State) {
        _state.update(reducer)
    }
}
