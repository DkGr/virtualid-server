package fr.virtualid.server.bean

import org.bson.codecs.pojo.annotations.BsonId
import java.util.*


data class User(
    @BsonId val id: UUID = UUID.randomUUID(),
    val username: String,
    val passwordHash: String,
    val email: String = ""
)