package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    companion object {
        private const val DELAY_FACTOR = 2
    }

    private var currentDelay = 100L

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { cause, attempt ->
                if (cause != null) {
                    delay(currentDelay)
                    currentDelay *= DELAY_FACTOR
                }
                true
            }
    }
}