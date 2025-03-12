package dev.mathroda.twelvereader.ui.screens.mainplayer

sealed interface MainPlayerUiActions {
    data object Play: MainPlayerUiActions
    data object Pause: MainPlayerUiActions
    data object Resume: MainPlayerUiActions
    data class SeekTo(val position: Long): MainPlayerUiActions
    data class SetSpeed(val speed: Float): MainPlayerUiActions
}