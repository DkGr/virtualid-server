package fr.eline.virtualid.mongo.extensions

import com.mongodb.DBRef
import fr.eline.virtualid.bean.User
import fr.eline.virtualid.dbName
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import java.util.*
import kotlin.collections.LinkedHashMap

@Target(AnnotationTarget.FIELD)
annotation class DBRef

@Target(AnnotationTarget.CLASS)
annotation class Collection(val name: String)

fun getCollectionName(obj: Any) : String? {
    val classItem = obj::class.java
    for (annotation in classItem.annotations) {
        if (classItem.isAnnotationPresent(Collection::class.java)) {
            return classItem.getAnnotation(Collection::class.java)?.name
        }
    }
    return null
}

fun analyzeDBRef(obj: Any) {
    val fields = obj::class.java.declaredFields
    for (field in fields) {
        field.isAccessible = true
        for (annotation in field.annotations) {
            val value = field.get(obj)
            //Search @DBRef annotation
            if (field.isAnnotationPresent(fr.eline.virtualid.mongo.extensions.DBRef::class.java)) {
                val objFields = value::class.java.declaredFields
                for (objField in objFields) {
                    if(objField.name == "id") {
                        objField.isAccessible = true
                        val bsonIdValue = objField.get(value)
                        val id = bsonIdValue as UUID
                        val colName = getCollectionName(value)
                        field.set(obj, DBRef(colName, id.toString()))
                    }
                }
            }
        }
    }
}

suspend fun includeDBRefs(obj: Any, client : CoroutineClient) {
    val fields = obj::class.java.declaredFields
    for (field in fields) {
        field.isAccessible = true
        val value = field.get(obj)
        if(value is LinkedHashMap<*, *>) {
            val user = client.getDatabase(dbName)
                .getCollection<User>("users")
                .findOne(User::id eq UUID.fromString(value["\$id"] as String))
            field.set(obj, user)
        }
    }
}