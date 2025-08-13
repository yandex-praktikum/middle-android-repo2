package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    companion object {
        private const val INITIAL_DELAY = 500L
        private const val DELAY_FACTOR = 2
        private const val MAX_RETRIES = 3
    }

    fun getReplyMessage(): Flow<String> {
        var currentDelay = INITIAL_DELAY
        return api.getReply()
            .retryWhen { cause, attempt ->
                if (cause is Exception && attempt < MAX_RETRIES) {
                    delay(currentDelay)
                    currentDelay *= DELAY_FACTOR
                    true
                } else {
                    false
                }
            }
    }
}