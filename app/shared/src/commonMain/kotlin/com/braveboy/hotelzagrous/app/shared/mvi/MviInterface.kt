package com.braveboy.hotelzagrous.app.shared.mvi

import kotlinx.coroutines.flow.StateFlow

interface MviViewModel<State, Intent> {
    val state: StateFlow<State>
    fun onIntent(intent: Intent)
}
