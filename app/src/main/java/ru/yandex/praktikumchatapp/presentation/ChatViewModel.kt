package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableStateFlow(emptyList<Message>())
    val messages = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collect { response ->
                    addMessage(Message.OtherMessage(response))
                }
            }
        }
    }

    fun sendMyMessage(text: String) {
        addMessage(Message.MyMessage(text))
    }

    private fun addMessage(message: Message) {
        _messages.update { currentMessages ->
            // логика добавления нового сообщения в _messages
            currentMessages + message
        }
    }
}