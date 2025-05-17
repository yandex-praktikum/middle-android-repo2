package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    companion object {
        private const val DEFAULT_DELAY = 100L
        private const val DELAY_FACTOR = 2
        private const val MAX_RETRIES = 3
    }

    fun getReplyMessage(): Flow<String> {
        var currentDelay = DEFAULT_DELAY
        var retryCount = 0

        return api.getReply()
            .retryWhen { cause, _ ->
            if (cause != null && retryCount < MAX_RETRIES) {
                retryCount++
                delay(currentDelay)
                currentDelay *= DELAY_FACTOR
                true
            } else {
                false
            }
        }
    }
}