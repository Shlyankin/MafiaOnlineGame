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

fun printInfo(room: Room, isMafia: Boolean) {
    println("Alive Users:")
    printUsers(room.aliveUsers)
    println("Died Users:")
    printUsers(room.diedUsers)
    if(isMafia) {
        println("Mafia Users:")
        printUsers(room.getMafia())
    }
}

fun inputUserNumber(min: Int, max: Int): Int {
    var killNumber: Int? = null
    while (killNumber == null) {
        try {
            killNumber = readLine()?.toInt()
            while (killNumber!! < min || killNumber >= max)
            {
                print("No users with number $killNumber. Try again:")
                killNumber = readLine()?.toInt()
            }
        } catch (e: NumberFormatException) {
            print("input NUMBER:")
        }
    }
    return killNumber
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
        }
        // game started
        println("game started")
        user = room.getUserById(true, user.id!!)!!
        println("You are " + user.role)
        mainloop@while(room?.winner == null) {
            println("________________NEXT ROUND________________")
            printInfo(room!!, user.role == User.MAFIA)
            print("Input number of alive user, which you want to kill: ")
            var killNumber: Int = inputUserNumber(0, room.aliveUsers.size)
            Api.answer(room.id, user.id!!, room.aliveUsers[killNumber].id!!)
            println("Waiting other users...")
            do  {
                Thread.sleep(5000)
                room = Api.getRoom(room!!.id)
                if (room.winner != null) break@mainloop
            } while (room?.voting?.votingType == User.CIVIL)
            if (room?.getUserById(false, user.id!!) != null) {
                println("You died")
                break@mainloop
            }

            printInfo(room!!, user.role == User.MAFIA)
            println("Mafia voting!")
            if (user.role == User.MAFIA) {
                print("Input number of alive user, which you want to kill: ")
                var killNumber = inputUserNumber(0, room.aliveUsers.size)
                Api.answer(room.id, user.id!!, room.aliveUsers[killNumber].id!!)
            }

            println("Waiting...")
            do  {
                Thread.sleep(5000)
                room = Api.getRoom(room!!.id)
                if (room.winner != null) break@mainloop
            } while (room?.voting?.votingType == User.MAFIA)

            if (room?.getUserById(false, user.id!!) != null) {
                println("________________You died________________")
                break@mainloop
            }
        }
        while (room?.winner == null) {
            println("Waiting end of game...")
            Thread.sleep(15000)
            room = Api.getRoom(room!!.id)
            printInfo(room, user.role == User.MAFIA)
        }
        println("Winner: " + room?.winner)
        Api.close()
    }
}