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

//    private val _messages =
//        MutableStateFlow<List<Message>>(emptyList())  // TODO Задание 1: замените на Flow
//    val messages: StateFlow<List<Message>> = _messages


    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState

    // TODO Задание 3: добавьте состояние shouldShowKeyboard

    // TODO Задание 4: замените messages и shouldShowKeyboard на state

//    private val _shouldShowKeyboard = MutableStateFlow(false)
//    val shouldShowKeyboard: StateFlow<Boolean> = _shouldShowKeyboard

    init {
        viewModelScope.launch {
            if (isWithReplies) {
                repository.getReplyMessage().collect { response ->
                    _chatState.value = _chatState.value.let { state ->
                        val isFirstMessage = state.messages.isEmpty()

                        state.copy(
                            messages = state.messages + Message.OtherMessage(response),
                            shouldShowKeyboard = isFirstMessage
                        )
                    }
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        _chatState.value = _chatState.value.copy(
            messages = _chatState.value.messages + Message.MyMessage(messageText),
            shouldShowKeyboard = false
        )
    }
}