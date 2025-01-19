package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.PlaylistInput
import models.PlaylistService
import models.SongService

fun Application.configureRouting() {
    val songService = SongService(DBSettings.db)
    val playlistService = PlaylistService(DBSettings.db, songService)

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/playlists") {
            call.respond(playlistService.index())
        }

        post("/playlists") {
            val playlist = call.receive<PlaylistInput>()
            val playlistResult = playlistService.create(playlist)
            call.respond(HttpStatusCode.Created, playlistResult)
        }
    }

}
