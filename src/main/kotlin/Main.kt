import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import views.NavigationView
import views.Playlist
import views.PlaylistResult
import views.PlaylistsResult

object ApiClient {
    private const val BASE_URL = "http://localhost:8080"

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getPlaylists(): PlaylistsResult {
        val res: HttpResponse = httpClient.get("$BASE_URL/playlists") {
            accept(ContentType.Application.Json)
        }
        val playlistResults: PlaylistsResult = res.body()

        return playlistResults
    }

    suspend fun postPlaylist(playlist: Playlist): PlaylistResult {
        val res: HttpResponse = httpClient.post("$BASE_URL/playlists") {
            contentType(ContentType.Application.Json)
            setBody(playlist)
        }
        println(res.bodyAsText())
        val playlistResults: PlaylistResult = res.body()

        return playlistResults
    }
}

@Composable
@Preview
fun App() {
    // val playlists = remember { mutableStateListOf<Playlist>() }
    NavigationView(/*playlists*/)
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = Dp(2000f), height = Dp(1000f))
    ) {
        App()
    }
}
