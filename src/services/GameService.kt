package com.ershov.game.services

import com.ershov.game.models.Room
import com.ershov.game.models.Voice
import models.User
import java.util.*
import kotlin.collections.ArrayList

class GameService {
    val rooms: ArrayList<Room> = ArrayList()
    val usersPool: ArrayList<User> = ArrayList()

    fun findUsersRoom(userId: String): String? {
        for (room in rooms) {
            if (room.getUserById(true, userId) != null)
                return room.id
        }
        return null
    }

    fun findUserPositionById(id: String): Int? {
        for (position in 0 until usersPool.size)
            if (usersPool[position].id == id) return position
        return null
    }

    fun findRoomPositionById(id: String): Int? {
        for (position in 0 until rooms.size)
            if (rooms[position].id == id) return position
        return null
    }

    suspend fun getRoom(roomId: String): Room? {
        findRoomPositionById(roomId)?.let {
            return rooms[it]
        }
        return null
    }

    suspend fun sendAnswer(aggressorId: String, victimId: String, roomId: String): Boolean {
        findRoomPositionById(roomId)?.let {
            val room = rooms[it]
            if (room.voting.votingType == User.MAFIA &&
                room.getUserById(true, aggressorId)?.role != room.voting.votingType)
                return false
            return room.voting.sendVoice(
                Voice(room.getUserById(true, aggressorId)!!, room.getUserById(true, victimId)!!))
        }
        return false
    }

    suspend fun createUser(user: User): User {
        user.id = UUID.randomUUID().toString()
        usersPool.add(user)
        return user
    }

    suspend fun checkRoom(userId: String): Room? {
        findUsersRoom(userId)?.let {roomId ->
            findRoomPositionById(roomId)?.let { roomPosition ->
                return rooms[roomPosition]
            }
        }
        return if (usersPool.size >= playersInOneRoom) {
            val users = ArrayList<User>()
            val roomId = UUID.randomUUID().toString()
            users.addAll(usersPool.subList(0, playersInOneRoom))
            users.forEach { it.roomId = roomId }
            val room = Room(roomId, users, ArrayList())
            rooms.add(room)
            for (i in 0 until playersInOneRoom)
                usersPool.removeAt(0)
            room
        } else null
    }

    companion object {
        const val playersInOneRoom = 3
    }
}