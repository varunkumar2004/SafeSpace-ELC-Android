package com.varunkumar.safespace.auth.presentation

data class SignInState (
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
