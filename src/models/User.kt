package models


data class User(var name: String){
    var role: String = CIVIL
    var id : String? = null
    var roomId: String? = null

    companion object {
        const val MAFIA = "mafia"
        const val CIVIL = "civil"
    }

    constructor(): this("")

    override fun equals(other: Any?): Boolean {
        val other = other as User
        return other.id == this.id
    }
}