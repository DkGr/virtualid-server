package fr.virtualid.server.request

data class RegisterUserRequest(val username: String,
                           val email : String,
                           val password: String,
                           val publicKey: String,
                           val privateKey: String)

data class UpdateUserRequest(val id: String,
                           val username: String,
                           val email : String,
                           val password: String)

data class DeleteUserRequest(val username: String)