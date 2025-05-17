import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    // Проверка наличия сообщения в переменной messages
    @Test
    fun `send message should update messages with MyMessage`() = runTest {
        val message = Message.MyMessage("TestMessage")

        viewModel.sendMyMessage(message.text)
        testDispatcher.scheduler.advanceUntilIdle()  // Явное указание на testDispatcher

        val currentMessages = viewModel.messages.value
        assertEquals(listOf(message), currentMessages)
    }

    // Проверка отсутствия состояния гонки (конкурентные вызовы)
    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        val coroutineScope = CoroutineScope(Job() + testDispatcher)

        val sendingJobs = messagesToSend.map { message ->
            coroutineScope.launch {
                viewModel.sendMyMessage(message.text)
            }
        }
        sendingJobs.joinAll()
        testDispatcher.scheduler.advanceUntilIdle()

        val currentMessages = viewModel.messages.value
        assertEquals(messagesToSend.size, currentMessages.size)
        assertTrue(currentMessages.containsAll(messagesToSend))

        coroutineScope.cancel()
    }
}