package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    fun getReplyMessage(): Flow<String> {
        val INITIAL_DELAY = 1000L
        val DELAY_FACTOR = 2
        val MAX_RETRIES = 5

        var currentDelay = INITIAL_DELAY

        return api.getReply()
            .retryWhen { _, attempt ->
                if (attempt < MAX_RETRIES) {
                    delay(currentDelay)
                    currentDelay *= DELAY_FACTOR
                    true
                } else {
                    false
                }
            }
    }
}