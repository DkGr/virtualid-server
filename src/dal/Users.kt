package fr.eline.virtualid.dal

import fr.eline.virtualid.bean.User
import fr.eline.virtualid.dbName
import io.ktor.auth.Principal
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId
import java.util.*

object Users {

    private const val usersCollectionName : String = "users"

    suspend fun findUserById(id : String, client : CoroutineClient) : User? {
        return client.getDatabase(dbName)
            .getCollection<User>("users")
            .findOne(User::_id eq ObjectId(id).toId())
    }

}