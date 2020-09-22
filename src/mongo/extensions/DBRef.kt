package fr.eline.virtualid.mongo.extensions

import com.mongodb.DBRef
import fr.eline.virtualid.bean.User
import fr.eline.virtualid.dbName
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.litote.kmongo.id.WrappedObjectId
import org.litote.kmongo.id.toId
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
                    if(objField.name == "_id") {
                        objField.isAccessible = true
                        val bsonIdValue = objField.get(value) as WrappedObjectId<Any>
                        val colName = getCollectionName(value)
                        field.set(obj, DBRef(colName, bsonIdValue.id.toString()))
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
            if(value["\$ref"] as String == "users") {
                val user = client.getDatabase(dbName)
                        .getCollection<User>("users")
                        .findOne(User::_id eq ObjectId(value["\$id"] as String).toId())
                if (user != null) field.set(obj, user)
            }
        }
    }
}