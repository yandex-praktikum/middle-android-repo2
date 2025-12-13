package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages =
        MutableStateFlow<List<Message>>(emptyList())  // TODO Задание 1: замените на Flow
    val messages: StateFlow<List<Message>> = _messages

    // TODO Задание 3: добавьте состояние shouldShowKeyboard

    // TODO Задание 4: замените messages и shouldShowKeyboard на state

    private val _shouldShowKeyboard = MutableStateFlow(false)
    val shouldShowKeyboard: StateFlow<Boolean> = _shouldShowKeyboard

    init {
        viewModelScope.launch {
            if (isWithReplies) {
                repository.getReplyMessage().collect { response ->
                    val isFirstMessage = _messages.value.isEmpty()

                    _messages.value += Message.OtherMessage(response)

                    if (isFirstMessage) {
                        _shouldShowKeyboard.value = true
                    }
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        val currentMessages = _messages.value ?: emptyList()
        _messages.value = currentMessages + Message.MyMessage(messageText)
    }
}