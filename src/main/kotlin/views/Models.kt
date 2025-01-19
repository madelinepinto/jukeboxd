package views

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistsResult(val playlists: List<PlaylistResult>)

@Serializable
data class PlaylistResult(
    val id: Int,
    val title: String,
    val songs: List<SongResult>,
    val createdAt: String,
)

@Serializable
data class SongResult(
    val id: Int,
    val title: String,
    val artist: String,
    val playlistId: Int,
)

@Serializable
data class Playlist(
    val title: String,
    val songs: List<Song>
)

@Serializable
data class Song(
    val title: String,
    val artist: String
)
