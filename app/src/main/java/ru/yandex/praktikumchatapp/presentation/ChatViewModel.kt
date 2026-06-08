package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository
import ru.yandex.praktikumchatapp.presentation.model.ChatState

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

                    _state.update { currentState ->
                        currentState.copy(
                            messages = currentState.messages + Message.OtherMessage(response)
                        )
                    }

                    if (_state.value.messages.isNotEmpty() && !_state.value.shouldShowKeyboard) {
                        _state.update { currentState ->
                            currentState.copy(shouldShowKeyboard = true)
                        }
                    }
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        _state.update { currentState ->
            currentState.copy(
                messages = currentState.messages + Message.MyMessage(messageText)
            )
        }
    }
}