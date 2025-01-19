package models

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import models.PlaylistService.Playlists.defaultExpression
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Serializable
data class ExposedSong(val id: Int, val title: String, val artist: String, val playlistId: Int)

@Serializable
data class SongInput(val title: String, val artist: String)

class SongService(private val database: Database) {
    object Songs : Table("songs") {
        val id = Songs.integer("id").autoIncrement()
        val title = varchar("title", MAX_VARCHAR_LENGTH)
        val artist = varchar("artist_name", MAX_VARCHAR_LENGTH)
        val playlistId = reference("playlist_id", PlaylistService.Playlists.id)
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
        val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction (Dispatchers.IO, database) { block() }

    public suspend fun batchCreate(playlistId: Int, songs: List<SongInput>): List<ExposedSong> = dbQuery {
        Songs.batchInsert(songs) { exposedSong ->
            this[Songs.title] = exposedSong.title
            this[Songs.artist] = exposedSong.artist
            this[Songs.playlistId] = playlistId
        }.map {
            ExposedSong(it[Songs.id], it[Songs.title], it[Songs.artist], it[Songs.playlistId])
        }
    }

    suspend fun readPlaylistSongs(playlistId: Int): List<ExposedSong> {
        return dbQuery {
            Songs.selectAll()
                .where { Songs.playlistId eq playlistId }
                .map { ExposedSong(it[Songs.id], it[Songs.title], it[Songs.artist], it[Songs.playlistId]) }
        }
    }
}