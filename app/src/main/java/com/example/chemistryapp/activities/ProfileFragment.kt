package com.example.chemistryapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.chemistryapp.R
import com.example.chemistryapp.models.GetCourseResponse
import com.example.chemistryapp.models.LoginResponse
import com.example.chemistryapp.models.ProfileResponse
import com.example.chemistryapp.utils.Utility
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.t2r2.volleyexample.FileDataPart
import com.t2r2.volleyexample.VolleyFileUploadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProfileFragment : Fragment() {
    private var imageData: ByteArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        val profileImage = view.findViewById<ImageView>(R.id.imageView)
        val userName = view.findViewById<TextView>(R.id.username)
        val emailField = view.findViewById<TextInputEditText>(R.id.emailField)
        val phoneField = view.findViewById<TextInputEditText>(R.id.phoneField)
        val isVerified = view.findViewById<TextView>(R.id.roleText)
        val publishBtn = view.findViewById<Button>(R.id.updateProfilebtn)

        profileImage.setOnClickListener {
            openImagePicker()
        }

        publishBtn.setOnClickListener {
            updateProfilePicture()
            updateProfileInfo()
        }



        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = getProfile(context)
                withContext(Dispatchers.Main){
                    val userInfo = result.result[0].user


                    Glide.with(view).load(userInfo.avatarUrl).placeholder(R.drawable.placeholder).into(profileImage)
                    userName.text = userInfo.name
                    isVerified.text = userInfo.isVerified.toString()
                    emailField.setText(userInfo.email)
                    phoneField.setText(userInfo.phone)

                }
            } catch (e : Exception) {
                Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun openImagePicker(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri){
        val inputStream = context?.contentResolver?.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1){
            val imagedata = data?.data
            if(imagedata != null) {
                val profileImage = view?.findViewById<ImageView>(R.id.imageView)
                profileImage?.setImageURI(imagedata)
                createImageData(imagedata)
            }
        }
    }

    private suspend fun getProfile(context: Context) : ProfileResponse = suspendCoroutine { continuation ->
        val gson = Gson()
        val url = Utility.apiUrl + "/api/user/profile"
        val sharedPref : SharedPreferences = context.getSharedPreferences("my_app_pref", Context.MODE_PRIVATE)
        val user = sharedPref.getString(Utility.userKey, "")
        val token = gson.fromJson(user, LoginResponse::class.java).accessToken

        val request = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener { response ->
                val profileResponse = gson.fromJson(response.toString(), ProfileResponse::class.java)
                continuation.resume(profileResponse)
            },
            Response.ErrorListener { error ->
                continuation.resumeWithException(error)
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return  headers
            }
        }



        Volley.newRequestQueue(context).add(request)

    }

    private fun updateProfileInfo(){
        val gson = Gson()
        val sharedPref : SharedPreferences = requireContext().getSharedPreferences("my_app_pref", Context.MODE_PRIVATE)
        val user = sharedPref.getString(Utility.userKey, "")
        val token = gson.fromJson(user, LoginResponse::class.java).accessToken
        val queue = Volley.newRequestQueue(context)

        val request = object : StringRequest(
            Method.POST,
            Utility.apiUrl + "/api/user/profile/update",
            {
                // Display the first 500 characters of the response string.
                Toast.makeText(context, "Updated successfully",Toast.LENGTH_SHORT).show()
            },
            {
                Log.d("Update profile", "error")
            }
        ) {



            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return  headers
            }

            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                val emailField = view?.findViewById<TextInputEditText>(R.id.emailField)
                val phoneField = view?.findViewById<TextInputEditText>(R.id.phoneField)

                params["email"] = emailField?.getText().toString()
                params["phone"] = phoneField?.getText().toString()

                return params
            }
        }


        queue.add(request)
    }

    private fun updateProfilePicture(){
        val gson = Gson()
        val sharedPref : SharedPreferences = requireContext().getSharedPreferences("my_app_pref", Context.MODE_PRIVATE)
        val user = sharedPref.getString(Utility.userKey, "")
        val token = gson.fromJson(user, LoginResponse::class.java).accessToken

        if(imageData == null){
            Toast.makeText(context, "Image required", Toast.LENGTH_SHORT).show()
            return
        }
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            Utility.apiUrl + "/api/user/upload/avatar",
            {
                Log.d("post image","response is: $it")
            },
            {
                Log.d("post image", "error is: $it")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["userImage"] = FileDataPart("image", imageData!!, "jpeg")
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return  headers
            }

        }
        request.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }
        Volley.newRequestQueue(context).add(request)
    }
}