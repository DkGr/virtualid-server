package fr.virtualid.server

import fr.virtualid.server.controller.userRoutes
import com.fasterxml.jackson.databind.SerializationFeature
import fr.virtualid.server.bean.User
import fr.virtualid.server.controller.postRoutes
import fr.virtualid.server.dal.Users
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import me.avo.io.ktor.auth.jwt.sample.JwtConfig
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.installKoin
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Locations) {
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(DataConversion)

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
        header("X-Environment", "Dev")
    }

    installKoin {
        modules(module)
    }

    install(Authentication) {
        /**
         * Setup the JWT authentication to be used in [Routing].
         * If the token is valid, the corresponding [User] is fetched from the database.
         * The [User] can then be accessed in each [ApplicationCall].
         */
        jwt(name = "virtualid-user-auth") {
            verifier(JwtConfig.verifier)
            realm = "Virtual iD API"
            validate {
                val client : CoroutineClient by inject()
                val id = it.payload.getClaim("id").asString()
                Users.findUserById(id, client)
            }
        }
    }

    routing {
        userRoutes()
        postRoutes()
    }
}

val module = module {
    single { KMongo.createClient("mongodb://localhost:27017").coroutine }
}