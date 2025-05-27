package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.microseconds

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    companion object {
        private val DELAY_FACTOR = 2
        private val INITIAL_DELAY = 100 //ms
        private val MAX_ATTEMPT = 3
    }

    fun getReplyMessage(): Flow<String> {
        var tempDelay = INITIAL_DELAY
        return api.getReply().retryWhen {_,attempt->
            val delayTime = tempDelay * DELAY_FACTOR
            tempDelay *= DELAY_FACTOR
            delay(delayTime.microseconds)
            attempt < MAX_ATTEMPT
        }
    }
}