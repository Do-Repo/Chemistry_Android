package com.example.chemistryapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chemistryapp.utils.Utility
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)

        loginButton.setOnClickListener{
            loginUser()
        }

        registerButton.setOnClickListener{
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }
    }

    private fun loginUser(){
        val queue = Volley.newRequestQueue(applicationContext)

        val url = Utility.apiUrl + "/api/auth/login"

        val requestBody = JSONObject()
        requestBody.put("email", emailField.text.toString())
        requestBody.put("password", passwordField.text.toString())

        val request = JsonObjectRequest(Request.Method.POST, url, requestBody,
            {
                // Login successful, start the main activity.
                val sharedPref = getSharedPreferences("my_app_pref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("is_logged_in", true)
                editor.apply()

                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            },
            { error ->
                // Login failed, show an error message.
                Log.d("Login error: ",error.toString())
                Toast.makeText(this, "Login failed, please try again", Toast.LENGTH_SHORT).show()
            })

        request.retryPolicy = DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        queue.add(request)
    }
}