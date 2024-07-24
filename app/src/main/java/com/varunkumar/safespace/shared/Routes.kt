package com.varunkumar.safespace.shared

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Home : Routes("home")
    data object Chat : Routes("chat")
    data object Sense : Routes("sense")
    data object Camera : Routes("camera")
    data object Splash : Routes("splash")
}