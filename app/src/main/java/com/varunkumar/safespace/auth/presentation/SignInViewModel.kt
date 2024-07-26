package com.varunkumar.safespace.auth.presentation

import androidx.lifecycle.ViewModel
import com.varunkumar.safespace.auth.data.SignInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(): ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    private fun resetState() {
        _state.update { SignInState() }
    }

    override fun onCleared() {
        super.onCleared()
        resetState()
    }
}
