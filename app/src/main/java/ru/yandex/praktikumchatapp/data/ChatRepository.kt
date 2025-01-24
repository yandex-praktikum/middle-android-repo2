package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlin.math.pow

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    private var retryAttempt = 0

    fun getReplyMessage(
        firstRetryMillis: Long = 1000,
        retryDelayFactor: Double = 2.0,
        retries: Int = 3
    ): Flow<String> {
        return api.getReply()
            .onEach { retryAttempt = 0 }
            .retry { cause ->
                val retryDelay = firstRetryMillis * retryDelayFactor.pow(retryAttempt.toDouble())

                /*Log.d(
                    "mainLog",
                    "retrying attempt: ${retryAttempt + 1}, waiting for ${retryDelay.toLong()}ms"
                )*/

                delay(retryDelay.toLong())
                retryAttempt++
                cause is Exception && retryAttempt < retries
            }
    }
}