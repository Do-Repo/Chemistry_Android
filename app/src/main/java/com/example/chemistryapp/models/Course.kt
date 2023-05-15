package com.example.chemistryapp.models

data class Course(
    val __v: Int,
    val _id: String,
    val content: String,
    val createdAt: String,
    val likes: Int,
    val owner: String,
    val price: Int,
    val publicid: String,
    val tags: List<Any>,
    val thumbnail: String,
    val title: String,
    val updatedAt: String
)