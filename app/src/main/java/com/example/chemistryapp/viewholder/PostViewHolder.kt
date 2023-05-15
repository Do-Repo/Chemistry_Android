package com.example.chemistryapp.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chemistryapp.R
import com.example.chemistryapp.models.Course

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

     val postImageView : ImageView = itemView.findViewById(R.id.postImageView);
      val titleTextView : TextView = itemView.findViewById(R.id.titleTextView);
      val contentTextView : TextView = itemView.findViewById(R.id.contentTextView);

    fun bind(post: Course) {
        Glide.with(itemView.context).load(post.thumbnail).into(postImageView)
        titleTextView.text = post.title
        contentTextView.text = post.content
    }
}