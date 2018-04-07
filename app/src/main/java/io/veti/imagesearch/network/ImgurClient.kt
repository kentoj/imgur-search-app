package io.veti.imagesearch.network

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

object ImgurClient {
    val client = OkHttpClient()
    val photos = mutableListOf<Photo>()

    fun fetchImages(clearImages: Boolean = false, pageNumber: Int = 0, searchTerm: String = "cat"): Deferred<List<Photo>> {
        return async(CommonPool) {
            val url = "https://api.imgur.com/3/gallery/search/time/$pageNumber?q=${URLEncoder.encode(searchTerm, "utf-8")}"
            val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Client-ID 0066c078c219c47")
                    .header("User-Agent", "Image Search")
                    .build()



            val response = client.newCall(request).execute()
            if(clearImages) photos.clear()
            photos.addAll(parseImagesJson(JSONObject(response?.body()?.string())))
            photos
        }
    }

    fun parseImagesJson(jsonObject: JSONObject): MutableList<Photo> {
        val albums = jsonObject.getJSONArray("data")
        val photos = mutableListOf<Photo>()
        if(albums.length() == 0) return mutableListOf()
        val albumJson = albums.getJSONObject(0) // first album -- TODO remove hard coding
        val photosJson = albumJson.getJSONArray("images")

        for (i in 0..(photosJson.length() -1)) {
            val photoJson = photosJson.getJSONObject(i)
            photos.add(Photo(
                    id = photoJson.getString("id"),
                    title = albumJson.getString("title"),
                    link = albumJson.getString("link")))
        }
        return photos
    }
}


data class Photo(val id: String, val title: String, val link: String)
