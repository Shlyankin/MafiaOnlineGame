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
import java.lang.Exception

fun printUsers(users: List<User>) {
    for (i in users.indices) {
        println(i.toString() + ". " + users[i].name)
    }
}

fun main(args: Array<String>) {
    runBlocking {
        println("Mafia Online")
        print("Nickname: ")
        val name: String = readLine()!!
        print("Connection...")
        var user: User = Api.createUser(name)
        println("You are connected. Waiting other users...")
        var room: Room? = null
        while (room == null) {
            Thread.sleep(5000)
            try {
                room = Api.checkOrCreateRoom(user.id!!)
            } catch (e: ClientRequestException) {
                print("")
                // 404 NotFound while all users dont connect to the server
            }
            catch (e: Exception) {
                print("")
            }
        }
        // game started
        println("game started")
        user = room.getUserById(true, user.id!!)!!
        println("You are " + user.role)
        while(room?.winner == null) {
            println("________________NEXT ROUND________________")
            println("Alive Users:")
            printUsers(room!!.aliveUsers)
            println("Died Users:")
            printUsers(room.diedUsers)
            println("Civil voting!")
            print("Input number of alive user, which you want to kill: ")
            var killNumber: Int? = null
            while (killNumber == null) {
                try {
                    killNumber = readLine()?.toInt()
                } catch (e: NumberFormatException) {
                }
            }
            Api.answerCivil(room.id, user.id!!, room.aliveUsers[killNumber].id!!)
            println("Waiting other users...")
            do  {
                Thread.sleep(5000)
                room = Api.getRoom(room!!.id)
            } while (room?.voting?.votingType == User.CIVIL)

            println("Alive Users:")
            printUsers(room!!.aliveUsers)
            println("Died Users:")
            printUsers(room.diedUsers)
            println("Mafia voting!")
            if (user.role == User.MAFIA) {
                print("Input number of alive user, which you want to kill: ")
                var killNumber: Int? = null
                while (killNumber == null) {
                    try {
                        killNumber = readLine()?.toInt()
                    } catch (e: NumberFormatException) {
                    }
                }
                Api.answerMafia(room.id, user.id!!, room.aliveUsers[killNumber].id!!)
            }

            println("Waiting...")
            do  {
                Thread.sleep(5000)
                room = Api.getRoom(room!!.id)
            } while (room?.voting?.votingType == User.MAFIA)
        }
        println("Winner: " + room.winner)
        Api.close()
    }
}