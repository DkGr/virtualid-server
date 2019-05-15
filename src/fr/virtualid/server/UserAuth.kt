package fr.virtualid.server

import fr.virtualid.server.bean.User
import io.ktor.application.Application
import io.ktor.auth.UserPasswordCredential
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object UserAuth {

    private const val usersCollectionName : String = "users"

    suspend fun authenticate(credentials : UserPasswordCredential, client: CoroutineClient) : User? {
        val col = client?.getDatabase(dbName)
            ?.getCollection<User>(usersCollectionName)
        val hashed = hashPassword("SHA-512", credentials.password)
        return col?.findOne(User::username eq credentials.name, User::passwordHash eq hashed)
    }


    @Throws(NoSuchAlgorithmException::class)
    fun hashPassword(algorithm: String, str: String): String {
        val md = MessageDigest.getInstance(algorithm)
        md.update(str.toByteArray())
        val byteData = md.digest()

        //convert the byte to hex format method 1
        val hashCodeBuffer = StringBuffer()
        for (i in byteData.indices) {
            hashCodeBuffer.append(Integer.toString((byteData[i] and 0xff.toByte()) + 0x100, 16).substring(1))
        }
        return hashCodeBuffer.toString()
    }

}