package io.ktor.response

import io.ktor.content.*
import io.ktor.http.*
import io.ktor.util.*

interface ResponsePushBuilder {
    val url: URLBuilder
    val headers: ValuesMapBuilder
    var method: HttpMethod
    val versions: MutableList<Version>
}