package io.veti.imagesearch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class DisplayImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        val title = intent.getStringExtra(EXTRA_IMAGE_TITLE)
        findViewById<TextView>(R.id.image_title).apply {
            text = title
        }
        val photo = findViewById<ImageView>(R.id.photo)
        val photoId = intent.getStringExtra(EXTRA_IMAGE_ID)
        Picasso.with(photo.context).load("https://i.imgur.com/$photoId.jpg").into(photo)
    }
}
