package fr.virtualid.server.request

import com.mongodb.DBRef
import fr.virtualid.server.bean.User
import java.util.*

data class CreatePostRequest(val authorid: String,
                             val content : String,
                             val date: Date,
                             val visibility: String)

data class DeletePostRequest(val postid: String)