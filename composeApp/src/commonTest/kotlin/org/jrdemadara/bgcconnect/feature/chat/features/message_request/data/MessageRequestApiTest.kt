import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestApi

class MessageRequestApiTest {

 private fun createMockClient(status: HttpStatusCode, body: String): HttpClient {
  return HttpClient(MockEngine) {
   install(ContentNegotiation) {
    json()
   }
   engine {
    addHandler { request ->
     respond(
      content = ByteReadChannel(body),
      status = status,
      headers = headersOf(HttpHeaders.ContentType, "application/json")
     )
    }
   }
  }
 }

 @Test
 fun `returns message on Created response`() = runTest {
  val jsonBody = """{"message": "Request successful"}"""
  val client = createMockClient(HttpStatusCode.Created, jsonBody)
  val api = MessageRequestApi(client)

  val result = api.messageRequest(1, "token123")

  assertEquals("Request successful", result)
 }

 @Test
 fun `throws exception on Unauthorized`() = runTest {
  val client = createMockClient(HttpStatusCode.Unauthorized, "")
  val api = MessageRequestApi(client)

  val exception = assertFailsWith<Exception> {
   api.messageRequest(1, "bad_token")
  }

  assertEquals("Unauthorized: Invalid credentials.", exception.message)
 }

 @Test
 fun `throws exception on Conflict`() = runTest {
  val client = createMockClient(HttpStatusCode.Conflict, "")
  val api = MessageRequestApi(client)

  val exception = assertFailsWith<Exception> {
   api.messageRequest(1, "token123")
  }

  assertEquals("Message request already exists.", exception.message)
 }
}