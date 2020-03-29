package com.ershov.game

import com.ershov.game.services.GameService
import com.ershov.game.services.widget
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.gson.gson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking


fun main(args: Array<String>): Unit {
    // io.ktor.server.netty.EngineMain.main(args)
    val server = embeddedServer(
        Netty,
        port = 8080,
        module = Application::mymodule
    ).apply {
        start(wait = false)
    }
}

fun Application.mymodule() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }
    install(Routing){ widget(gameService = GameService()) }
}
