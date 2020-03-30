package com.ershov.game

import com.ershov.game.models.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.coroutines.runBlocking
import models.User
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parametersOf

object Api {
    var client: HttpClient = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = GsonSerializer {
                    // .GsonBuilder
                    serializeNulls()
                    disableHtmlEscaping()
                }
            }
        }

    suspend fun createUser(username: String): User = client.post<User> {
        url("http://127.0.0.1:8080/user/new")
        contentType(ContentType.Application.Json)
        body = User(username)
    }

    suspend fun checkOrCreateRoom(userId: String): Room = client.get<Room> {
        url("http://127.0.0.1:8080/user/getRoom/{userId}")
        parameter("userId", userId)
    }

    suspend fun getRoom(roomId: String): Room = client.get<Room> {
        url("http://127.0.0.1:8080/game/room/{roomId}")
        parameter("roomId", roomId)
    }

    suspend fun answer(roomId: String, aggressorId: String, victimId: String) {
        client.post<Unit> {
            url("http://127.0.0.1:8080/game/room/answer/{roomId}&{aggressorId}&{victimId}")
            parameter("aggressorId", aggressorId)
            parameter("victimId", victimId)
            parameter("roomId", roomId)
        }
    }

    fun reopen() {
        client = HttpClient(io.ktor.client.engine.apache.Apache) {
            install(io.ktor.client.features.json.JsonFeature) {
                serializer = io.ktor.client.features.json.GsonSerializer {
                    // .GsonBuilder
                    serializeNulls()
                    disableHtmlEscaping()
                }
            }
        }
    }

    fun close() {
        client.close()
    }
}