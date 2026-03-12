package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    fun getReplyMessage(): Flow<String> {
        var errorCount = 0

        return api.getReply()
            .onEach {
                errorCount = 0
            }
            .retryWhen { cause, _ ->
                if (cause is Exception) {
                    errorCount++
                    delay(errorCount * DELAY_STEP_MS)
                }
                true
            }
    }

    companion object {
        private const val DELAY_STEP_MS = 2000L
    }
}