package com.ershov.game.services

import com.ershov.game.models.Room
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sun.org.apache.xpath.internal.operations.Bool
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.http.cio.websocket.*
import models.User
import org.apache.http.HttpStatus

const val WIDGET_END_POINT_GAME = "/game"
const val WIDGET_END_POINT_USER = "/user"
val mapper = jacksonObjectMapper().apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }

fun Route.widget(gameService: GameService){

    route(WIDGET_END_POINT_GAME) {
        get("/room/{roomId}") {
            val room = gameService.getRoom(call.parameters["roomId"]?.toString()!!)
            if (room != null) call.respond(room)
            else call.respond(HttpStatusCode.NotFound)
        }

        post("/room/answerCivil/{roomId}&{aggressorId}&{victimId}") {
            val roomId = call.parameters["roomId"]?.toString()!!
            val aggressorId = call.parameters["aggressorId"]?.toString()!!
            val victimId = call.parameters["victimId"]?.toString()!!
            val isSend = gameService.sendAnswer(aggressorId, victimId, roomId, User.CIVIL)
            if (isSend) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }

        post("/room/answerMafia/{roomId}&{aggressorId}&{victimId}") {
            val roomId = call.parameters["roomId"]?.toString()!!
            val aggressorId = call.parameters["aggressorId"]?.toString()!!
            val victimId = call.parameters["victimId"]?.toString()!!
            val isSend = gameService.sendAnswer(aggressorId, victimId, roomId, User.MAFIA)
            if (isSend) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.NotFound)
        }
    }


    route(WIDGET_END_POINT_USER){
        post("/new") {
            val user = call.receive<User>()
            call.respond(HttpStatusCode.Created, gameService.createUser(user))
        }

        get("/getRoom/{userId}") {
            val userId: String = call.parameters["userId"]?.toString()!!
            val room: Room? = gameService.checkRoom(userId)
            if(room == null)
                call.respond(HttpStatusCode.NotFound)
            else
                call.respond(room)
        }
    }
}