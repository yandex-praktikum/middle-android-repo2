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

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.getReplyMessage().collect { response ->
                    _messages.update { currentMessages ->
                        currentMessages + Message.OtherMessage(response)
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun sendMyMessage(text: String) {
        _messages.update { currentMessages ->
            currentMessages + Message.MyMessage(text)
        }
    }
}