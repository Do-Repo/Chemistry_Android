package com.example.chemistryapp.adapters

import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chemistryapp.R
import com.example.chemistryapp.activities.CourseActivity
import com.example.chemistryapp.activities.HomeFragment
import com.example.chemistryapp.models.Course
import com.example.chemistryapp.viewholder.PostViewHolder
import com.google.gson.Gson

class PostAdapter(private val course: List<Course> ) : RecyclerView.Adapter<PostViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = course[position]
        holder.titleTextView.text = post.title
        holder.contentTextView.text = post.content

        Glide.with(holder.itemView)
            .load(post.thumbnail)
            .placeholder(R.drawable.placeholder)
            .into(holder.postImageView)

        holder.itemView.setOnClickListener {
            val gson = Gson()
            val postJson = gson.toJson(post)
            val intent = Intent(holder.itemView.context, CourseActivity::class.java)
                intent.apply {
                    putExtra("course", postJson)
                }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return course.size
    }
}