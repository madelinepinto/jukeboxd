package views

import ApiClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.runBlocking

object FeedTab : Tab {
    @Composable
    override fun Content() {
        val playlists = LocalPlaylists.current

        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                // Display the list of playlists
                if (playlists.value.isEmpty()) {
                    Text("No playlists posted yet.")
                } else {
                    PlaylistsList(playlists)
                }
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = remember { "Feed" }

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                )
            }
        }
}
