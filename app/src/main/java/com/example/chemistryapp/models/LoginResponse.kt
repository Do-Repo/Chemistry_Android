package com.example.chemistryapp.models

data class LoginResponse(
    val accessToken: String,
    val extras: Extras,
    val status: String,
    val user: User
)