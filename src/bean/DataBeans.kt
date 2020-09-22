package fr.eline.virtualid.bean

import fr.eline.virtualid.mongo.extensions.DBRef
import fr.eline.virtualid.mongo.extensions.Collection
import io.ktor.auth.Principal
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.util.*

@Collection("users")
data class User(
    @BsonId val _id: Id<User> = newId(),
    val username: String = "",
    val passwordHash: String = "",
    val email: String = "",
    val publicKey: String = "",
    val privateKey: String = ""
) : Principal

@Collection("posts")
data class Post(
    @BsonId val _id: Id<Post> = newId(),
    @DBRef val author: Any,
    val date: Date,
    val content: String = "",
    val visibility: String = "friends"
)

