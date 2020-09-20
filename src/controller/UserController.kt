package fr.eline.virtualid.controller

import fr.eline.virtualid.JwtConfig
import fr.eline.virtualid.UserAuth
import fr.eline.virtualid.bean.User
import fr.eline.virtualid.dbName
import fr.eline.virtualid.request.*
import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

fun Route.userRoutes() {

    val logger: Logger = LoggerFactory.getLogger("UserController")
    val client: CoroutineClient by inject()

    val collectionName = "users"

    route("/users") {

        authenticate("virtualid-user-auth") {
            get("/list") {
                val users = client.getDatabase(dbName)
                    .getCollection<User>(collectionName)
                    .find()
                    .toList()
                call.respond(HttpStatusCode.OK, users)
            }

            post<UpdateUserRequest>("/update") { request ->
                val user = User(
                    id = UUID.fromString(request.id),
                    username = request.username,
                    email = request.email,
                    passwordHash = UserAuth.hashPassword("SHA-512", request.password)
                )

                client.getDatabase(dbName)
                    .getCollection<User>(collectionName)
                    .updateOne(User::username eq request.username, user)

                logger.info("User ${request.username} informations updated")
                call.respond(HttpStatusCode.OK)
            }

            post<DeleteUserRequest>("/delete") { request ->
                client.getDatabase(dbName)
                    .getCollection<User>(collectionName)
                    .deleteOne(User::username eq request.username)

                logger.info("User ${request.username} deleted")
                call.respond(HttpStatusCode.OK)
            }
        }

        /**
         * A public login [Route] used to obtain JWTs
         */
        post("login") {
            val client : CoroutineClient by this@route.inject()
            val credentials = call.receive<UserPasswordCredential>()
            val user = UserAuth.authenticate(credentials, client)
            if(user == null) call.respond(HttpStatusCode.Forbidden)
            else {
                val token = JwtConfig.makeToken(user)
                call.respondText("{\"token\":\"${token}\"}", ContentType.Application.Json)
            }
        }

        post<RegisterUserRequest>("/register") { request ->
            val user = User(
                username = request.username,
                email = request.email,
                passwordHash = UserAuth.hashPassword("SHA-512", request.password),
                publicKey = request.publicKey,
                privateKey = request.privateKey
            )
            client.getDatabase(dbName)
                .getCollection<User>(collectionName)
                .insertOne(user)

            logger.info("New user ${request.username} registered")
            call.respond(HttpStatusCode.OK)
        }
    }
}



