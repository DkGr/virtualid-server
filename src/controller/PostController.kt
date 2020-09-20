package fr.eline.virtualid.controller

import fr.eline.virtualid.bean.Post
import fr.eline.virtualid.bean.User
import fr.eline.virtualid.dbName
import fr.eline.virtualid.mongo.extensions.analyzeDBRef
import fr.eline.virtualid.mongo.extensions.includeDBRefs
import fr.eline.virtualid.request.*
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.koin.ktor.ext.inject
import org.litote.kmongo.ascending
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

fun Route.postRoutes() {

    val logger: Logger = LoggerFactory.getLogger("PostController")
    val client: CoroutineClient by inject()

    val collectionName = "posts"

    route("/posts") {

        authenticate("virtualid-user-auth") {
            get("/list") {
                val posts = client.getDatabase(dbName)
                    .getCollection<Post>(collectionName)
                    .find()
                    .sort(descending(Post::date))
                    .toList()
                for (post in posts){
                    includeDBRefs(post, client)
                }
                call.respond(HttpStatusCode.OK, posts)
            }

            post<CreatePostRequest>("/create") { request ->
                val post = Post(
                    author = User(UUID.fromString(request.authorid)),
                    content = request.content,
                    visibility = request.visibility,
                    date = request.date
                )
                analyzeDBRef(post)

                client.getDatabase(dbName)
                    .getCollection<Post>(collectionName)
                    .insertOne(post)

                logger.info("New post ${post.id} published")
                call.respond(HttpStatusCode.OK)
            }

            post<DeletePostRequest>("/delete") { request ->
                client.getDatabase(dbName)
                    .getCollection<Post>(collectionName)
                    .deleteOne(User::id eq UUID.fromString(request.postid))

                logger.info("Post ${request.postid} deleted")
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}