package com.example.chemistryapp.models

data class GetCourseResponse(
    val courses: List<Course>,
    val status: String
)