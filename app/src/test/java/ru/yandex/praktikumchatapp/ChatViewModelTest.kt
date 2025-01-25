import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel
    private val messagesToSend = List(100) { "Message $it" }

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
        val message = Message.MyMessage("TestMessage")

        viewModel.sendMyMessage(message.text)

        viewModel.messages.test {
            val emittedMessages = awaitItem()
            assertThat(emittedMessages.size, equalTo(1))
            assertThat(emittedMessages[0], equalTo(message))
        }
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }

        coroutineScope {
            val jobs = messagesToSend.map { message ->
                launch(testDispatcher) {
                    viewModel.sendMyMessage(message.text)
                }
            }
            jobs.joinAll()
        }

        viewModel.messages.test {
            val emittedMessages = awaitItem()
            assertThat(emittedMessages.size, equalTo(messagesToSend))
            emittedMessages.forEachIndexed { index, message ->
                assertThat(message, equalTo(messagesToSend[index]))
            }
        }

    }
}