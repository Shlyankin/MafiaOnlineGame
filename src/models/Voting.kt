package com.ershov.game.models

import models.User
import kotlin.random.Random

class Voting(val requireVoice: Int, val onVotingEnd: ((loser: User) -> Unit?)? = null, val votingType: String) {
    private val voices = ArrayList<Voice>()

    fun sendVoice(voice: Voice): Boolean {
        for (voice_i in voices)
            if (voice.aggressor == voice_i.aggressor)
                return false
        voices.add(voice)
        if (voices.size == requireVoice)
            allAnswersGetted()
        return true
    }

    private fun allAnswersGetted() {
        val voting = Array(voices.size) {
            var numberOfVoice = 0
            for (voice in voices)
                if (voice.victim == voices[it].aggressor) numberOfVoice++
            numberOfVoice
        }
        var diedUser = 0
        for (i in voting.indices)
            if (voting[diedUser] < voting[i])
                diedUser = i
        val diedUsers = ArrayList<Int>()
        for (i in voting.indices)
            if (voting[diedUser] == voting[i])
                diedUsers.add(i)
        diedUser = diedUsers[Random.nextInt(diedUsers.size)]
        onVotingEnd?.let { it(voices[diedUser].aggressor) }
    }
}