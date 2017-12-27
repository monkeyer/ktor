package io.ktor.sessions

import io.ktor.application.*
import io.ktor.response.*

class SessionTransportHeader(val name: String,
                             val transformers: List<SessionTransportTransformer>
) : SessionTransport {
    override fun receive(call: ApplicationCall): String? {
        return transformers.transformRead(call.request.headers[name])
    }

    override fun send(call: ApplicationCall, value: String) {
        call.response.header(name, transformers.transformWrite(value))
    }

    override fun clear(call: ApplicationCall) {}
}