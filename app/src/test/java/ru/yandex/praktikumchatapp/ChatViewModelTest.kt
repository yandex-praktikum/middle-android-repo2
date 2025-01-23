import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

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
    fun `send message should update messages with MyMessage`() = runTest(testDispatcher) {
        val sentMessage = Message.MyMessage("TestMessage")
        viewModel.sendMyMessage(sentMessage.text)

        val actualMessages = viewModel.messages.value

        assertEquals(1, actualMessages.size)
        assertEquals(sentMessage, actualMessages.last())
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest(testDispatcher) {
        val n = 1000

        // creating dispatcher on 8 threads, so we can reproduce race condition
        val dispatcher = Executors.newFixedThreadPool(8).asCoroutineDispatcher()

        val jobs = mutableListOf<Job>()
        (1..n).map {
            jobs += launch(dispatcher) {
                val sentMessage = Message.MyMessage("TestMessage $it")
                viewModel.sendMyMessage(sentMessage.text)
            }
        }

        jobs.joinAll()

        val actualMessages = viewModel.messages.value

        val set = mutableSetOf<String>()
        for (message in actualMessages) {
            if (message is Message.MyMessage) {
                set.add(message.text)
            }
        }

        //checking for missing or duplicated messages in viewModel
        assertEquals(n, set.size)
    }
}