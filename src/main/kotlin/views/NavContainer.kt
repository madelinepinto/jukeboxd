package views

import ApiClient
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.runBlocking
import widgets.SideNav

val LocalPlaylists = compositionLocalOf { mutableStateOf<List<PlaylistResult>>(mutableListOf()) }

@Composable
fun NavigationView(/*playlists: MutableList<Playlist>*/) {
    val playlistsResult = runBlocking {
        ApiClient.getPlaylists()
    }
    val playlists = remember { mutableStateOf<List<PlaylistResult>>(playlistsResult.playlists) }
    CompositionLocalProvider(LocalPlaylists provides playlists) {
        TabNavigator(FeedTab) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SideNav(listOf(FeedTab, ProfileTab))
                Spacer(Modifier.width(15.dp))
                CurrentTab()
            }
        }
    }
}
