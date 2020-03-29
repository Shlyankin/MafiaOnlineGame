package com.ershov.game.models

import models.User
import kotlin.random.Random

data class Room(val id: String, val aliveUsers: ArrayList<User>, val diedUsers: ArrayList<User>, var winner: String? = null) {

    private fun nextVoting() {
        winner = checkWinner()
        voting = if (winner == null) {
            if (voting.votingType == User.CIVIL) Voting(getMafia().size, onVotingEnd, User.MAFIA)
            else Voting(aliveUsers.size, onVotingEnd, User.CIVIL)
        } else Voting(0, null, User.CIVIL)
    }

    private val onVotingEnd = { diedUser: User ->
        killUser(diedUser.id!!)
        nextVoting()
    }

    var voting: Voting = Voting(aliveUsers.size, onVotingEnd, User.CIVIL)

    init {
        startGame()
    }

    fun getMafia(): List<User> {
        val mafia = ArrayList<User>()
        for (user in aliveUsers)
            if (user.role == User.MAFIA)
                mafia.add(user)
        return mafia
    }

    fun startGame() {
        val mafia1 = Random.nextInt(0, aliveUsers.size)
        var mafia2 = Random.nextInt(0, aliveUsers.size)
        while (mafia1 == mafia2)
            mafia2 = Random.nextInt(0, aliveUsers.size)
        aliveUsers[mafia1].role = User.MAFIA
        aliveUsers[mafia2].role = User.MAFIA
    }

    fun getUserById(isAlive: Boolean, id: String): User? {
        if (isAlive) {
            for (user in aliveUsers)
                if (user.id == id) return user
        } else {
            for (user in diedUsers)
                if (user.id == id) return user
        }
        return null
    }

    private fun killUser(id: String) {
        getUserById(true, id)?.let {diedUser: User ->
            if(aliveUsers.remove(diedUser))
                diedUsers.add(diedUser)
        }
    }

    private fun checkWinner(): String? {
        var isMafia = true
        var isCivil = true
        for (user in aliveUsers) {
            if (user.role == User.CIVIL) isMafia = false
            if (user.role == User.MAFIA) isCivil = false
        }
        return when {
            isMafia -> {
                User.MAFIA
            }
            isCivil -> {
                User.CIVIL
            }
            else -> null
        }
    }
}