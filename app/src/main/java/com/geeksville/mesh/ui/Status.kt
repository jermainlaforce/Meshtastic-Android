package com.geeksville.mesh.ui

import androidx.compose.Model
import com.geeksville.mesh.R


data class ScreenInfo(val icon: Int, val label: String)

// defines the screens we have in the app
object Screen {
    val settings = ScreenInfo(R.drawable.ic_twotone_settings_applications_24, "Settings")
    val channel = ScreenInfo(R.drawable.ic_twotone_contactless_24, "Channel")
    val users = ScreenInfo(R.drawable.ic_twotone_people_24, "Users")
    val messages = ScreenInfo(R.drawable.ic_twotone_message_24, "Messages")
}


@Model
object AppStatus {
    var currentScreen: ScreenInfo = Screen.messages
}


/**
 * Temporary solution pending navigation support.
 */
fun navigateTo(destination: ScreenInfo) {
    AppStatus.currentScreen = destination
}
