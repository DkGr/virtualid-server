package fr.eline.virtualid.dal

import fr.eline.virtualid.bean.User
import fr.eline.virtualid.dbName
import io.ktor.auth.Principal
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import java.util.*

object Users {

    private const val usersCollectionName : String = "users"

    suspend fun findUserById(id : String, client : CoroutineClient) : User? {
        return client.getDatabase(dbName)
            .getCollection<User>("users")
            .findOne(User::id eq UUID.fromString(id))
    }

}