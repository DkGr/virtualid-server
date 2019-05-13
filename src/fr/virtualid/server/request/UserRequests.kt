package fr.virtualid.server.request

data class RegisterRequest(val username: String,
                           val email : String,
                           val password: String)

data class UpdateRequest(val username: String,
                           val email : String,
                           val password: String)

data class DeleteRequest(val username: String)