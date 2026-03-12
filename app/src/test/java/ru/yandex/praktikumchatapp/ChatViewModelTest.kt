import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
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

    @Test
    fun `send message should update state with MyMessage`() = runTest {
        val message = Message.MyMessage("TestMessage")

        viewModel.chatState.test {
            assert(awaitItem().messages.isEmpty())
            viewModel.sendMyMessage(message.text)
            assert(awaitItem().messages.first() == message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        val scope = CoroutineScope(Job() + testDispatcher)

        val jobs = messagesToSend.map { message ->
            scope.launch {
                viewModel.sendMyMessage(message.text)
            }
        }
        jobs.joinAll()

        viewModel.chatState.test {
            val state = awaitItem()
            assert(state.messages.size == messagesToSend.size)
            state.messages.forEachIndexed { index, message ->
                assert(message == messagesToSend[index])
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}