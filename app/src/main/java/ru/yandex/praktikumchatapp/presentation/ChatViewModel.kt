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

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collect { response ->
                    _state.update {
                        it.copy(
                            messages = it.messages + Message.OtherMessage(response),
                            shouldShowKeyboard = response.isNotEmpty() || it.shouldShowKeyboard
                        )
                    }
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    messages = it.messages + Message.MyMessage(messageText)
                )
            }
        }
    }
}