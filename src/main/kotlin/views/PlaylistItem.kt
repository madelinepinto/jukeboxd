package views


import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PlaylistItem(playlist: PlaylistResult) {
    val timestamp = LocalDateTime.parse(playlist.createdAt)
    val formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy hh:mm a")
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "from @goz31",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "created ${timestamp.format(formatter)}",
                style = MaterialTheme.typography.subtitle2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            playlist.songs.forEach { song ->
                Text(
                    text = "- ${song.title}, ${song.artist}",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}
