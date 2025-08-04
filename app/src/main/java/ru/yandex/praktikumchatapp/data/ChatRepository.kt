package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlin.math.min

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { exception, attempt ->
                var currentDelay = 100L
                val maxDelay = 1000L

                if (exception is Exception) {
                    currentDelay = min(currentDelay * (1 shl attempt.toInt()), maxDelay)
                    delay(currentDelay)
                    true
                } else {
                    false
                }
            }
    }
}