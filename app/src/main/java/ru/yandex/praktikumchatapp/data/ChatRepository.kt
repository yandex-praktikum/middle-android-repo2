package ru.yandex.praktikumchatapp.data

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    fun getReplyMessage(): Flow<String> {
        var currentDelay = 1000L
        return api.getReply()
            .retryWhen { _, attempt ->
                if (attempt < 3) {
                    delay(currentDelay)
                    currentDelay *= 2
                    true
                } else {
                    false
                }
            }
            .catch { e ->
                Log.e("ChatRepository", "Ошибка запроса", e)
            }
    }
}