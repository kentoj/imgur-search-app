package io.veti.studentsearch.network

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object ImgurClient {
    val client = OkHttpClient()

    fun fetchImages(): Deferred<List<Photo>> {
        return async(CommonPool) {
            val url = "https://api.imgur.com/3/gallery/user/rising/0.json";
            val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Client-ID 0066c078c219c47")
                    .header("User-Agent", "StudentSearch")
                    .build()



            val response = client.newCall(request).execute()
            parseImagesJson(JSONObject(response?.body()?.string()))
        }
    }

    fun parseImagesJson(jsonObject: JSONObject): List<Photo> {
        val items = jsonObject.getJSONArray("data")
        val photos = mutableListOf<Photo>()

        for (i in 0..(items.length() - 1)) {
            val item = items.getJSONObject(i)
            var id = if (item.getBoolean("is_album")) item.getString("cover") else item.getString("id")
            photos.add(Photo(id = id, title = item.getString("title"), link = item.getString("link")))
        }
        return photos
    }
}


data class Photo(val id: String, val title: String, val link: String)
