package fr.virtualid.server.controller

import fr.virtualid.server.bean.User
import fr.virtualid.server.dbName
import fr.virtualid.server.request.DeleteRequest
import fr.virtualid.server.request.RegisterRequest
import fr.virtualid.server.request.UpdateRequest
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.getDigestFunction
import org.koin.ktor.ext.inject
import org.litote.kmongo.addToSet
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import com.oracle.util.Checksums.update
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and


fun Route.userRoutes() {

    val logger: Logger = LoggerFactory.getLogger("UserController")
    val client: CoroutineClient by inject()

    val collectionName = "users"

    route("/users") {

        get("/list") {
            val users = client.getDatabase(dbName)
                .getCollection<User>(collectionName)
                .find()
                .toList()
            call.respond(HttpStatusCode.OK, users)
        }

        post<UpdateRequest>("/update"){ request ->
            val user = User(
                id = UUID.fromString(request.id),
                username = request.username,
                email = request.email,
                passwordHash = getHashCodeFromString("SHA-512", request.password)
            )

            client.getDatabase(dbName)
                .getCollection<User>(collectionName)
                .updateOne(User::username eq request.username, user)

            logger.info("User ${request.username} informations updated")
            call.respond(HttpStatusCode.OK)
        }

        post<RegisterRequest>("/register") { request ->
            val user = User(
                username = request.username,
                email = request.email,
                passwordHash = getHashCodeFromString("SHA-512", request.password),
                publicKey = request.publicKey,
                privateKey = request.privateKey
            )
            client.getDatabase(dbName)
                .getCollection<User>(collectionName)
                .insertOne(user)

            logger.info("New user ${request.username} registered")
            call.respond(HttpStatusCode.OK)
        }

        post<DeleteRequest>("/delete"){ request ->
            client.getDatabase(dbName)
                .getCollection<User>(collectionName)
                .deleteOne(User::username eq request.username)

            logger.info("User ${request.username} deleted")
            call.respond(HttpStatusCode.OK)
        }
    }
}

@Throws(NoSuchAlgorithmException::class)
private fun getHashCodeFromString(algorithm: String, str: String): String {
    val md = MessageDigest.getInstance(algorithm)
    md.update(str.toByteArray())
    val byteData = md.digest()

    //convert the byte to hex format method 1
    val hashCodeBuffer = StringBuffer()
    for (i in byteData.indices) {
        hashCodeBuffer.append(Integer.toString((byteData[i] and 0xff.toByte()) + 0x100, 16).substring(1))
    }
    return hashCodeBuffer.toString()
}



