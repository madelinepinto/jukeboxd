package views

import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import spotify.getSpotifyAccessToken
import spotify.searchSongs

@Composable
fun SongItem(
    song: Song,
    onSongChange: (Song) -> Unit,
    onRemove: () -> Unit
) {
    var title by remember { mutableStateOf(song.title) }
    var artist by remember { mutableStateOf(song.artist) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = song.title,
                onValueChange = {
                    title = it
                    onSongChange(song.copy(title = it)) },
                label = { Text("Song Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = song.artist,
                onValueChange = {
                    artist = it
                    onSongChange(song.copy(artist = it)) },
                label = { Text("Artist") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        IconButton(onClick = onRemove) {
            androidx.compose.material.Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove Song"
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    val clientId = "a57c21eed7a242e7ab8390f8ca620dba"
    val clientSecret = "f900e450dd024470a435ded7d1807479"
    if (title.isNotBlank() || artist.isNotBlank()) {
        getSpotifyAccessToken(clientId, clientSecret) { accessToken ->
            if (accessToken != null) {
                searchSongs("$title $artist", accessToken)
            } else {
                println("Failed to obtain access token.")
            }
        }
    }
}
