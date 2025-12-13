package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { cause, attempt ->
                if (attempt < 3) {
                    val delayTime = 1000L * (attempt + 1)
                    delay(delayTime)
                    true
                } else {
                    false
                }
            }
    }

//    fun getReplyMessage(): Flow<String> {
//        return api.getReply()  // TODO Задание 2: добавьте обработку ошибок
//    }
}