package org.jetbrains.ktor.gson.tests

import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.gson.*
import org.jetbrains.ktor.http.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.testing.*
import org.junit.*
import kotlin.test.*

class GsonTest {
    @Test
    fun testMap() = withTestApplication {
        application.install(GsonSupport)
        application.routing {
            val model = mapOf("id" to 1, "title" to "Hello, World!")
            get("/") {
                call.respondJson(model)
            }
        }

        handleRequest(HttpMethod.Get, "/") {
            addHeader("Accept", "application/json")
        }.response.let { response ->
            assertNotNull(response.content)
            assertEquals(listOf("""{"id":1,"title":"Hello, World!"}"""), response.content!!.lines())
            val contentTypeText = assertNotNull(response.headers[HttpHeaders.ContentType])
            assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), ContentType.parse(contentTypeText))
        }
    }

    @Test
    fun testEntity() = withTestApplication {
        application.install(GsonSupport) {

        }
        application.routing {
            val model = MyEntity(777, "Cargo", listOf(ChildEntity("Qube", 1), ChildEntity("Sphere", 2)))

            get("/") {
                call.respondJson(model)
            }
        }

        handleRequest(HttpMethod.Get, "/"){
            addHeader("Accept", "application/json")
        }.response.let { response ->
            assertNotNull(response.content)
            assertEquals(listOf("""{"id":777,"name":"Cargo","children":[{"item":"Qube","quantity":1},{"item":"Sphere","quantity":2}]}"""), response.content!!.lines())
            val contentTypeText = assertNotNull(response.headers[HttpHeaders.ContentType])
            assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), ContentType.parse(contentTypeText))
        }
    }
}

data class MyEntity(val id: Int, val name: String, val children: List<ChildEntity>)
data class ChildEntity(val item: String, val quantity: Int)

