package ru.yandex.praktikumchatapp.presentation.model

import ru.yandex.praktikumchatapp.presentation.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val shouldShowKeyboard: Boolean = false
)