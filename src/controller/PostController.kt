package fr.eline.virtualid.controller

import fr.eline.virtualid.bean.Post
import fr.eline.virtualid.bean.User
import fr.eline.virtualid.dal.Users
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
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject
import org.litote.kmongo.ascending
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId
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
                    author = User(ObjectId(request.authorid).toId()),
                    content = request.content,
                    visibility = request.visibility,
                    date = request.date
                )
                if (post != null) {
                    analyzeDBRef(post)
                    client.getDatabase(dbName)
                        .getCollection<Post>(collectionName)
                        .insertOne(post)
                    logger.info("New post ${post._id} published")
                    call.respond(HttpStatusCode.OK)
                }
                else call.respond(HttpStatusCode.InternalServerError)
            }

            post<DeletePostRequest>("/delete") { request ->
                client.getDatabase(dbName)
                    .getCollection<Post>(collectionName)
                    .deleteOne(Post::_id eq ObjectId(request.postid).toId())

                logger.info("Post ${request.postid} deleted")
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}