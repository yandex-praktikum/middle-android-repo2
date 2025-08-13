import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
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

    @Test
    fun `send message should update messages with MyMessage`() = runTest {
        val messageText = "TestMessage"

        viewModel.sendMyMessage(messageText)

        val messages = viewModel.messages.value
        assert(messages.size == 1)
        assert(messages.first() == Message.MyMessage(messageText))

    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { "Message $it" }

        coroutineScope {
            messagesToSend.forEach { msg ->
                launch {
                    viewModel.sendMyMessage(msg)
                }
            }
        }

        val messages = viewModel.messages.value
        assert(messages.size == 100)
        val allTexts = messages.map { (it as Message.MyMessage).text }
        assert(allTexts.containsAll(messagesToSend))

    }
}