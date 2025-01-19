package spotify
import okhttp3.*
import java.io.IOException
import org.json.JSONObject

val clientId = "a57c21eed7a242e7ab8390f8ca620dba"
val clientSecret = "f900e450dd024470a435ded7d1807479"

fun main() {
    getSpotifyAccessToken(clientId, clientSecret) { accessToken ->
        if (accessToken != null) {
            println("Access Token: $accessToken")
            searchSongs("Shape of You", accessToken)
        } else {
            println("Failed to obtain access token.")
        }
    }
}

fun getSpotifyAccessToken(clientId: String, clientSecret: String, callback: (String?) -> Unit) {
    val client = OkHttpClient()
    val formBody = FormBody.Builder()
        .add("grant_type", "client_credentials")
        .add("client_id", clientId)
        .add("client_secret", clientSecret)
        .build()

    val request = Request.Builder()
        .url("https://accounts.spotify.com/api/token")
        .post(formBody)
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
            callback(null)
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val token = parseAccessToken(responseBody)
                callback(token)
            } else {
                println("Error: ${response.message}")
                callback(null)
            }
        }
    })
}

fun searchSongs(query: String, accessToken: String) {
    val client = OkHttpClient()
    val encodedQuery = query.replace(" ", "%20")
    val request = Request.Builder()
        .url("https://api.spotify.com/v1/search?q=$encodedQuery&type=track&limit=1")
        .get()
        .addHeader("Authorization", "Bearer $accessToken")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val trackDetails = parseSearchResults(responseBody)
                if (trackDetails != null) {
                    val (trackId, coverArtUrl) = trackDetails
                    println("Track ID: $trackId")
                    println("Cover Art URL: $coverArtUrl")
                } else {
                    println("No results found.")
                }
            } else {
                println("Error: ${response.message}")
            }
        }
    })
}

fun parseAccessToken(responseBody: String?): String? {
    responseBody?.let {
        val json = JSONObject(it)
        return json.optString("access_token", null)
    }
    return null
}

fun parseSearchResults(responseBody: String?): Pair<String?, String?>? {
    responseBody?.let {
        val jsonObject = JSONObject(it)
        val tracks = jsonObject.getJSONObject("tracks")
        val items = tracks.getJSONArray("items")
        if (items.length() > 0) {
            val track = items.getJSONObject(0)
            val trackId = track.getString("id")
            val album = track.getJSONObject("album")
            val coverArtUrl = album.getJSONArray("images").getJSONObject(0).getString("url")
            return Pair(trackId, coverArtUrl)
        }
    }
    return null
}
