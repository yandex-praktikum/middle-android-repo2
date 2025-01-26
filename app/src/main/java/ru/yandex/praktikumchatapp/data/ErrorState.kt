package ru.yandex.praktikumchatapp.data

sealed class ErrorState {
    data class NetworkError(val message: String) : ErrorState()
    object NoConnection : ErrorState()
    object Timeout : ErrorState()
}