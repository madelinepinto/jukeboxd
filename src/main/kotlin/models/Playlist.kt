package models

import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Serializable
data class PlaylistsResult(val playlists: List<ExposedPlaylist>)

@Serializable
data class ExposedPlaylist(val id: Int, val title: String, val songs: List<ExposedSong>, val createdAt: String)

@Serializable
data class PlaylistInput(val title: String, val songs: List<SongInput>)

class PlaylistService(private val database: Database, private val songService: SongService) {
    object Playlists : Table("playlists") {
        val id = Playlists.integer("id").autoIncrement()
        val title = varchar("title", MAX_VARCHAR_LENGTH)
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
        val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    }

    suspend fun <T> dbQuery(block: suspend (Transaction) -> T): T =
        newSuspendedTransaction (Dispatchers.IO, database) { block(this) }

    suspend fun create(playlistInput: PlaylistInput): ExposedPlaylist = dbQuery {
        val id = Playlists.insert { row ->
            row[title] = playlistInput.title
        }[Playlists.id]

        it.commit()

        val newPlaylistRow = Playlists.selectAll().where(Playlists.id eq id).single()

        val exposedSongs = songService.batchCreate(id, playlistInput.songs)
        ExposedPlaylist(id, playlistInput.title, exposedSongs, newPlaylistRow[Playlists.createdAt].toString())
    }

    suspend fun index(): PlaylistsResult {
        data class PlaylistHolder(val title: String, val createdAt: String, val songs: MutableList<ExposedSong>)
        return dbQuery {
            val playlistMap = hashMapOf<Int, PlaylistHolder>()
            SongService.Songs.join(
                Playlists, JoinType.INNER, SongService.Songs.playlistId, Playlists.id
            ).selectAll().forEach {
                val songsTable = SongService.Songs
                val playlistId = it[Playlists.id]
                playlistMap.putIfAbsent(
                    playlistId,
                    PlaylistHolder(
                        it[Playlists.title],
                        it[Playlists.createdAt].toString(),
                        mutableListOf()
                    )
                )

                if (playlistMap.containsKey(playlistId)) {
                    val x = playlistMap[playlistId]!!.songs.add(
                        ExposedSong(
                            it[songsTable.id],
                            it[songsTable.title],
                            it[songsTable.artist],
                            it[songsTable.playlistId],
                        )
                    )
                }
            }

            PlaylistsResult(playlistMap.map { (playlistId, p) ->
                ExposedPlaylist(playlistId, p.title, p.songs, p.createdAt)
            }.sortedByDescending { it.createdAt })
        }
    }
}