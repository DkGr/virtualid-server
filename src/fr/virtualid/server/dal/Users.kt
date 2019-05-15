package fr.virtualid.server.dal

import fr.virtualid.server.bean.User
import fr.virtualid.server.dbName
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