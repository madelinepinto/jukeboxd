package com.example.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import models.ExposedUser
import models.UserService

@Serializable
data class OauthUserInfo(
    val id: String,
    val email: String,
    val name: String,
    val given_name: String,
    val family_name: String,
    val picture: String
)

fun Application.configureSecurity() {
    val httpClient = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    val clientId = System.getenv("GOOGLE_CLIENT_ID") ?: ""
    val clientSecret = System.getenv("GOOGLE_CLIENT_SECRET") ?: ""
    val userService = UserService(DBSettings.db)

    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile")
                )
            }
            client = httpClient
        }
    }

    // TODO: Refactor this into Routing.kt if possible
    routing {
        authenticate("auth-oauth-google") {
            get("/login") {
                // Redirects to 'authorizeUrl' automatically
            }

            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()

                if (principal != null) {
                    // val client = HttpClient(Apache)
                    val userInfoEndpoint = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json"

                    // Fetch user info from Google API
                    val userInfoResponse: HttpResponse = httpClient.get(userInfoEndpoint) {
                        header("Authorization", "Bearer ${principal.accessToken}")
                    }

                    println(userInfoResponse.bodyAsText())

                    // Deserialize
                    val userInfoJson: OauthUserInfo = userInfoResponse.body()

                    val existingUser = userService.findByOauthUser(userInfoJson.id)
                    val retUser: ExposedUser
                    val status: HttpStatusCode
                    if (existingUser == null) {
                        // create user
                        println("!! ADDING TO DB")
                        val newUserId = userService.insertIgnore(userInfoJson.id, userInfoJson.email, userInfoJson.given_name, userInfoJson.family_name)
                        retUser = ExposedUser(userInfoJson.given_name, userInfoJson.family_name, newUserId)
                        status = HttpStatusCode.Created
                    } else {
                        println("!! FETCHING FROM DB")
                        retUser = existingUser
                        status = HttpStatusCode.OK
                    }

                    call.respond(status, retUser)
                } else {
                    // TODO: explain why failed
                    call.respondText("OAuth login failed.")
                }
            }
        }
    }
}
