package fr.eline.virtualid

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.google.gson.GsonBuilder
import fr.eline.virtualid.bean.User
import java.util.*

object JwtConfig {
    private const val secret = "Sm9E1La8dXfKzrD3Yw9RNOZLP4A8g1Nb"
    private const val issuer = "virtualid.fr"
    private const val validityInMs = 36_000_00 * 10 // 10 hours
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build()

    /**
     * Produce a token for this combination of User and Account
     */
    fun makeToken(user: User): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", user.id.toString())
            .withExpiresAt(getExpiration())
            .sign(algorithm)
    }

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}