package com.example.chemistryapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chemistryapp.utils.Utility
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameField : EditText
    private lateinit var phoneField : EditText
    private lateinit var emailField : EditText
    private lateinit var passwordField : EditText
    private lateinit var passwordConfirmField : EditText
    private lateinit var registerButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameField = findViewById(R.id.name)
        phoneField = findViewById(R.id.phone)
        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        passwordConfirmField = findViewById(R.id.passwordConfirm)
        registerButton = findViewById(R.id.register_button)

        registerButton.setOnClickListener{
            registerUser()
        }
    }

    private fun registerUser(){


        if(passwordField.text.toString() == passwordConfirmField.text.toString()){
            val queue = Volley.newRequestQueue(applicationContext)

            val url = Utility.apiUrl + "/api/auth/register"

            val requestBody = JSONObject()
            requestBody.put("name", nameField.text.toString())
            requestBody.put("phone", phoneField.text.toString())
            requestBody.put("email", emailField.text.toString())
            requestBody.put("password", passwordField.text.toString())
            requestBody.put("passwordConfirm", passwordConfirmField.text.toString())

            val request = JsonObjectRequest(Request.Method.POST, url, requestBody,
                {
                    val sharedPref = getSharedPreferences("my_app_pref", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putBoolean("is_logged_in", true)
                    editor.apply()

                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                { error ->
                    Log.d("Register error:", error.toString())
                    Toast.makeText(this, "Registration failed, please try again", Toast.LENGTH_SHORT).show()
                })

            request.retryPolicy = DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            queue.add(request)
        }else{
            Toast.makeText(this, "Passwords don't match, please confirm", Toast.LENGTH_SHORT).show()
        }

    }
}