package fr.virtualid.server.bean

import fr.virtualid.server.mongo.extensions.Collection
import fr.virtualid.server.mongo.extensions.DBRef
import io.ktor.auth.Principal
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

@Collection("users")
data class User(
    @BsonId val id: UUID = UUID.randomUUID(),
    val username: String = "",
    val passwordHash: String = "",
    val email: String = "",
    val publicKey: String = "",
    val privateKey: String = ""
) : Principal

@Collection("posts")
data class Post(
    @BsonId val id: UUID = UUID.randomUUID(),
    @DBRef val author: Any,
    val date: Date,
    val content: String = "",
    val visibility: String = "friends"
)

