package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

object DBSettings {
    val db by lazy {
        val dbHost = System.getenv("DB_HOST") ?: ""
        val dbPort = System.getenv("DB_PORT") ?: ""
        val url = "jdbc:postgresql://$dbHost:$dbPort/?rewriteBatchedStatements=true"
        println("Connecting to $url...")

        val dbUser = System.getenv("DB_USER") ?: ""
        val dbPassword = System.getenv("DB_PASSWORD") ?: ""

        Database.connect(
            url = url,
            user = dbUser,
            password = dbPassword,
            driver = "org.postgresql.Driver"
        )
    }
}

fun Application.configureDatabases() {
    routing {
    }
}
