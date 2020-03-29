package com.ershov.game.models

import models.User

data class Voice(val aggressor: User, val victim: User) {
}