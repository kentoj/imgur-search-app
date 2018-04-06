package io.veti.studentsearch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import io.veti.studentsearch.experimental.Android
import io.veti.studentsearch.network.ImgurClient
import io.veti.studentsearch.network.Photo
import kotlinx.coroutines.experimental.launch
import java.io.IOException

class MainActivity : AppCompatActivity(), PhotoClickListener {


    private lateinit var posts: RecyclerView
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var postsLayoutManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        posts = findViewById(R.id.posts_list) as RecyclerView
        postsAdapter = PostsAdapter(listener = this)
        postsLayoutManager = LinearLayoutManager(this)

        posts.apply {
            setHasFixedSize(true)
            layoutManager = postsLayoutManager
            adapter = postsAdapter
        }
    }


    override fun onPostClicked(photo: Photo) {
        Toast.makeText(this, "Clicked ${photo.id}", Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()

        launch(Android) {
            try {
                val postsResult = ImgurClient.fetchImages()

                postsAdapter.setElements(postsResult.await())
                postsAdapter.notifyDataSetChanged()
            } catch(exception: IOException) {
                Toast.makeText(this@MainActivity, "Phone not connected or service down", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
