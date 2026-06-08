package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlin.math.pow

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { cause, attempt ->
                if (cause is Exception && attempt < REPEAT_NUMBER) {
                    val currentDelay = INITIAL_DELAY_MS * 2.0.pow(attempt.toDouble()).toLong()
                    delay(currentDelay)
                    true
                } else {
                    false
                }
            }

    }

    companion object {
        private const val REPEAT_NUMBER = 3
        private const val INITIAL_DELAY_MS = 1000L
    }
}