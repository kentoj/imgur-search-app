package io.veti.imagesearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import io.veti.imagesearch.experimental.Android
import io.veti.imagesearch.network.ImgurClient
import io.veti.imagesearch.network.Photo
import kotlinx.coroutines.experimental.launch
import java.io.IOException
import java.util.*

const val EXTRA_IMAGE_ID = "io.veti.imagesearch.IMAGE_ID"

class MainActivity : AppCompatActivity(), PhotoClickListener {



    private lateinit var photosView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var postsLayoutManager: RecyclerView.LayoutManager
    private var pageNumber = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photosView = findViewById(R.id.posts_list) as RecyclerView
        photoAdapter = PhotoAdapter(listener = this)
        postsLayoutManager = LinearLayoutManager(this)

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!recyclerView.canScrollVertically(1)) {
                    pageNumber += 1
                    getImages()
                }
            }
        }

        photosView.addOnScrollListener(scrollListener)

        photosView.apply {
            setHasFixedSize(true)
            layoutManager = postsLayoutManager
            adapter = photoAdapter
        }

        val searchText = findViewById<EditText>(R.id.search_text)
        searchText.addTextChangedListener(object: TextWatcher {
            var timer = Timer()
            val delay = 250L

            override fun afterTextChanged(p0: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        pageNumber = 0
                        getImages(clearImages = true)
                    }
                }, delay)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Log.i("SEARCH", "before value changed")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Log.i("SEARCH", "changing value to $p0")
            }

        })

    }


    private fun hideKeyboard() {
        val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }


    override fun onPhotoClicked(photo: Photo, view: View) {
        Toast.makeText(this, "Clicked ${photo.id}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DisplayImageActivity::class.java).apply {
            putExtra(EXTRA_IMAGE_ID, photo.id)
        }
        startActivity(intent)
    }


    private fun getImages(clearImages: Boolean = false) {
        launch(Android) {
            try {
                val searchText = findViewById<EditText>(R.id.search_text)
                searchText.text.toString()
                val postsResult = ImgurClient.fetchImages(clearImages = clearImages, pageNumber = pageNumber, searchTerm = searchText.text.toString())

                photoAdapter.setElements(postsResult.await())
                photoAdapter.notifyDataSetChanged()
                Log.i("SEARCH", "Data changed")
                hideKeyboard()
            } catch(exception: IOException) {
                Toast.makeText(this@MainActivity, "Phone not connected or service down", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        getImages()
    }

}
