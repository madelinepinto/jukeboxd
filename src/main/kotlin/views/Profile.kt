package views

import ApiClient
import java.awt.Desktop
import java.net.URI
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


@Composable
fun LazyScrollable() {
    val playlists = LocalPlaylists.current
    var showDialog by remember { mutableStateOf(false) }
    var loggedIn by remember { mutableStateOf(true) }
    var showBrowserMessage by remember { mutableStateOf(false) }
    val url = "https://accounts.google.com" // Change to localhost:8080/login

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(10.dp)
    ) {
        val state = rememberLazyListState()

        if (!loggedIn && !showBrowserMessage) {
            // Display only the "Login with Google" button when not logged in
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // "Login with Google" button
                Button(onClick = {
                    // Open the browser to the Google login
                    try {
                        Desktop.getDesktop().browse(URI(url))
                        showBrowserMessage = true // Show the message after opening the browser
                    } catch (e: Exception) {
                        e.printStackTrace() // Handle the error if browser fails to open
                    }
                }) {
                    Text("Login with Google")
                }
            }
        } else if (showBrowserMessage) {
            // Show message to check the browser
            LaunchedEffect(showBrowserMessage) {
                delay(5000) // Wait for 5 seconds
                showDialog = true // Show the content after 5 seconds
                loggedIn = true // Assume logged in after delay
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Please check your browser for login.", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Trouble logging in? Enter $url into your browser.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.primary
                )
            }

        } else {
            // Playlist creation dialog
            if (showDialog) {
                PlaylistCreationDialog(
                    onDismiss = { showDialog = false },
                    onPost = { playlist ->
                        runBlocking {
                            ApiClient.postPlaylist(playlist)
                        }
                        val allPlaylists = runBlocking {
                            ApiClient.getPlaylists()
                        }
                        playlists.value = allPlaylists.playlists
                        showDialog = false
                    }
                )
            }

            LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Rounded.AccountCircle,
                            contentDescription = "",
                            modifier = Modifier.padding(top = 30.dp).size(300.dp),
                        )
                        Spacer(Modifier.height(30.dp))
                        Text("@goz31", fontSize = 22.sp)
                    }
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 60.dp, start = 10.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text("My Playlists:", fontSize = 30.sp)

                        // "Post Playlist" button
                        Button(onClick = { showDialog = true }) {
                            Text("Create New Playlist")
                        }

                        Spacer(Modifier.height(20.dp))
                        for (playlist in playlists.value) {
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
                                        text = playlist.songs.size.toString() + " songs",
                                        style = MaterialTheme.typography.subtitle2
                                    )
                                }
                            }
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}

object ProfileTab : Tab {
    @Composable
    override fun Content() {
        MaterialTheme {
            LazyScrollable()
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = remember { "Profile" }

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                )
            }
        }
}