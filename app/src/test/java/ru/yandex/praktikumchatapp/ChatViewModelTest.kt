import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    fun `send message should update state with MyMessage`() = runTest {
        val text = "TestMessage"

        viewModel.sendMyMessage(text)

        val state = viewModel.chatState.value
        val message = state.messages.first()

        val textMessage =message as Message.MyMessage
        assert(textMessage.text == text)
    }



    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }

        messagesToSend.forEach { message ->
            launch {
                viewModel.sendMyMessage(message.text)
            }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.chatState.value

        assert(state.messages.size == messagesToSend.size)

        messagesToSend.forEach { sentMessage ->
            assert(
                state.messages.any { received ->
                    received is Message.MyMessage &&
                            received.text == sentMessage.text
                }
            )
        }

    }

}