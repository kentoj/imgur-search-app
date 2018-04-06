package io.veti.imagesearch

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import io.veti.imagesearch.network.Photo

class PhotoAdapter(var photos : List<Photo> = ArrayList(),
                   val listener: PhotoClickListener) : RecyclerView.Adapter<PhotoViewHolder>(){

    fun setElements(elements : List<Photo>){
        photos = elements
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.id.text = photo.id
        holder.title.text = photo.title
        holder.link.text = photo.link
        holder.view.setOnClickListener { listener.onPhotoClicked(photo) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PhotoViewHolder(
                view = view,
                id = view.findViewById(R.id.id) as TextView,
                title = view.findViewById(R.id.title) as TextView,
                link = view.findViewById(R.id.link) as TextView)
    }

    override fun getItemCount(): Int {
        return photos.size
    }


}

interface PhotoClickListener{
    fun onPhotoClicked(photo : Photo)
}

class PhotoViewHolder(val view: View, val id: TextView, val title : TextView, val link: TextView) : RecyclerView.ViewHolder(view)
