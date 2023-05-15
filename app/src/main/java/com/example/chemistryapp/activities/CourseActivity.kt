package com.example.chemistryapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.chemistryapp.R
import com.example.chemistryapp.models.Course
import com.google.gson.Gson

class CourseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        val gson = Gson()
        val course = getIntent().getStringExtra("course")

        val courseFromJson =  gson.fromJson(course, Course::class.java)

        val image = findViewById<ImageView>(R.id.imageView4)
        val title = findViewById<TextView>(R.id.courseTitle)
        val contents = findViewById<TextView>(R.id.courseContents)

        Glide.with(this)
            .load(courseFromJson.thumbnail)
            .placeholder(R.drawable.illustration_started)
            .into(image)

        title.text = courseFromJson.title

        contents.text = courseFromJson.content
    }
}