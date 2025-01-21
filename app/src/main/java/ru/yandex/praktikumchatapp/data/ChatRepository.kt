package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    fun getReplyMessage(): Flow<String> {
        return api.getReply().retryWhen { cause, attempt ->
            delay(attempt * DELAY_FACTOR)
            true
        }
    }

    private companion object {
        private const val DELAY_FACTOR = 5000L
    }
}