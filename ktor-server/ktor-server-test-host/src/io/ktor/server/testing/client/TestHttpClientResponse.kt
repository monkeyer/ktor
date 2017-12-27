package io.ktor.server.testing.client

import io.ktor.client.call.*
import io.ktor.client.response.*
import io.ktor.content.*
import io.ktor.http.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.*
import java.util.*


class TestHttpClientResponse(
        override val call: HttpClientCall,
        override val status: HttpStatusCode,
        override val headers: Headers,
        private val content: ByteArray
) : HttpResponse {
    override val requestTime = Date()
    override val responseTime = Date()
    override val version = HttpProtocolVersion.HTTP_1_1

    override val executionContext: CompletableDeferred<Unit> = CompletableDeferred()

    override fun receiveContent(): IncomingContent = object : IncomingContent {
        override val headers: Headers = this@TestHttpClientResponse.headers
        override fun readChannel(): ByteReadChannel = ByteReadChannel(content)
        override fun multiPartData(): MultiPartData = throw UnsupportedOperationException()
    }

    override fun close() {
        executionContext.complete(Unit)
        (call.request.executionContext as CompletableDeferred<Unit>).complete(Unit)
    }
}
