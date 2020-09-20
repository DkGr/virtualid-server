package fr.eline.virtualid.request

import com.mongodb.DBRef
import fr.eline.virtualid.bean.User
import java.util.*

data class CreatePostRequest(val authorid: String,
                             val content : String,
                             val date: Date,
                             val visibility: String)

data class DeletePostRequest(val postid: String)