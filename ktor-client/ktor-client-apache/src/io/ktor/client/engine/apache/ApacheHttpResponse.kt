package io.ktor.client.engine.apache

import io.ktor.client.call.*
import io.ktor.client.response.*
import io.ktor.content.*
import io.ktor.http.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.*
import java.util.*


class ApacheHttpResponse internal constructor(
        override val call: HttpClientCall,
        override val requestTime: Date,
        override val executionContext: CompletableDeferred<Unit>,
        private val engineResponse: org.apache.http.HttpResponse,
        private val content: ByteReadChannel
) : HttpResponse {
    override val status: HttpStatusCode
    override val version: HttpProtocolVersion
    override val headers: Headers
    override val responseTime: Date = Date()

    init {
        val code = engineResponse.statusLine.statusCode

        status = HttpStatusCode.fromValue(code)
        version = with(engineResponse.protocolVersion) { HttpProtocolVersion.fromValue(protocol, major, minor) }
        headers = HeadersBuilder().apply {
            engineResponse.allHeaders.forEach { headerLine ->
                append(headerLine.name, headerLine.value)
            }
        }.build()
    }

    override fun receiveContent(): IncomingContent = object : IncomingContent {
        override val headers: Headers = this@ApacheHttpResponse.headers

        override fun readChannel(): ByteReadChannel = content

        override fun multiPartData(): MultiPartData = throw UnsupportedOperationException()
    }

    override fun close() {
        executionContext.complete(Unit)
    }
}
