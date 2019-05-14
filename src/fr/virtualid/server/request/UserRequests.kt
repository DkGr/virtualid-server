package fr.virtualid.server.request

data class RegisterRequest(val username: String,
                           val email : String,
                           val password: String,
                           val publicKey: String,
                           val privateKey: String)

data class UpdateRequest(val id: String,
                           val username: String,
                           val email : String,
                           val password: String)

data class DeleteRequest(val username: String)