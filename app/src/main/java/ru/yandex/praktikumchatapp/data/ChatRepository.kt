package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    fun getReplyMessage(): Flow<String> {
        var currentDelay = 1000L
        return api.getReply()
            .retryWhen { _, _ ->
                delay(currentDelay)
                currentDelay *= DELAY_FACTOR
                true
            }
    }

    companion object {
        private const val DELAY_FACTOR = 2
    }
}