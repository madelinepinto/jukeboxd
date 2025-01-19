package views

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState


@Composable
fun PlaylistsList(playlists: MutableState<List<PlaylistResult>>) {
    LazyColumn {
        items(playlists.value) { playlist ->
            PlaylistItem(playlist)
        }
    }
}

