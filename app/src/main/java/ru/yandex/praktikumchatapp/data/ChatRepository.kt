package ru.yandex.praktikumchatapp.data

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlin.math.exp

class ChatRepository(
    private val api: ChatApi = ChatApi(),
) {

    companion object {
        private const val MAX_RETRIES = 3
        private const val LOG_TAG = "ChatRepository"
    }

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { cause, attempt ->
                if (cause is Exception) {
                    Log.e(LOG_TAG, "Retry attempt $attempt failed: ${cause.message}", cause)
                    val delayFactor = exp(attempt.toDouble()).toLong()
                    delay(delayFactor)
                    return@retryWhen attempt < MAX_RETRIES
                }
                false
            }
    }
}