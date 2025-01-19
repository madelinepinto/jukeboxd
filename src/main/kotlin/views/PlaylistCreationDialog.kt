package views


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaylistCreationDialog(
    onDismiss: () -> Unit,
    onPost: (Playlist) -> Unit
) {
    var playlistTitle by remember { mutableStateOf("") }
    val songs = remember { mutableStateListOf<Song>() }
    var isSearching by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Playlist") },
        text = {
            Column {
                // Playlist Title Input
                OutlinedTextField(
                    value = playlistTitle,
                    onValueChange = { playlistTitle = it },
                    label = { Text("Playlist Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Songs List
                Text("Songs:")
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn {
                    itemsIndexed(songs) { index, song ->
                        SongItem(
                            song = song,
                            onSongChange = { updatedSong ->
                                songs[index] = updatedSong
                            },
                            onRemove = {
                                songs.removeAt(index)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // "Add Song" Button
                Button(onClick = {
                    songs.add(Song(title = "", artist = ""))
                }) {
                    Text("Add Song")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (playlistTitle.isNotBlank() && songs.isNotEmpty()) {
                        onPost(Playlist(title = playlistTitle, songs = songs.toList()))
                    }
                }
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
