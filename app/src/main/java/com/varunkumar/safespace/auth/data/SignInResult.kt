package com.varunkumar.safespace.auth.data

import com.varunkumar.safespace.shared.UserData

data class SignInResult (
    val data: UserData?,
    val errorMessage: String?
)
