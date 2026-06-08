package ru.yandex.praktikumchatapp.presentation.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.yandex.praktikumchatapp.presentation.Message

@Immutable
data class ChatState(
    val messages: ImmutableList<Message> = persistentListOf(),
    val shouldShowKeyboard: Boolean = false
)