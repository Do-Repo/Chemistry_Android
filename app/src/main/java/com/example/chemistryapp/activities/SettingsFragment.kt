package com.example.chemistryapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.example.chemistryapp.R
import com.example.chemistryapp.models.LoginResponse
import com.example.chemistryapp.utils.Utility
import com.google.gson.Gson
import com.t2r2.volleyexample.FileDataPart
import com.t2r2.volleyexample.VolleyFileUploadRequest
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class SettingsFragment : Fragment() {

    private val client = OkHttpClient()
    private var imageData: ByteArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleBox = view.findViewById<EditText>(R.id.editCourseTitle)
        val autoGenerateBtn = view.findViewById<Button>(R.id.autoGenerate)
        val submitImageBtn = view.findViewById<Button>(R.id.button2)
        val publishBtn = view.findViewById<Button>(R.id.button5)
        val courseContent = view.findViewById<EditText>(R.id.courseContent)

        autoGenerateBtn.setOnClickListener {
            val command = "Write a 3 blocks long paragraph about ${titleBox.text}"
            getResponse(command) { response ->
                ThreadUtil.runOnUiThread {
                    courseContent.setText(response)
                }
            }
        }

        submitImageBtn.setOnClickListener {
            uploadImage()
        }

        publishBtn.setOnClickListener {
            postCourse()
        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1){
            val imagedata = data?.data
            if(imagedata != null) {
                val profileImage = view?.findViewById<ImageView>(R.id.imageView2)
                profileImage?.setImageURI(imagedata)
                createImageData(imagedata)
            }
        }
    }

    private fun postCourse(){
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
            Utility.apiUrl + "/api/course/create",
            {
                Log.d("post course","response is: $it")
            },
            {
                Log.d("post course", "error is: $it")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["thumbnail"] = FileDataPart("image", imageData!!, "jpeg")
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return  headers
            }

            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                val titleBox = view?.findViewById<EditText>(R.id.editCourseTitle)
                val courseContent = view?.findViewById<EditText>(R.id.courseContent)

                params["title"] = titleBox?.getText().toString()
                params["content"] = courseContent?.getText().toString()

                return params
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

    private fun uploadImage(){
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

    private fun getResponse(question: String, callback: (String) -> Unit){
        val apiKey = "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"
        val url = "https://api.openai.com/v1/completions"

        val requestBody = """
            {
            "model": "text-davinci-003",
            "prompt": "$question",
            "max_tokens": 30,
            "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "Api failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if(body != null) {
                    Log.v("data", body)
                }else{
                    Log.v("data", "empty")
                }
                val jsonObject = JSONObject(body)
                val jsonArray:JSONArray = jsonObject.getJSONArray("choices")
                val textResult = jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }

        })
    }
}

object ThreadUtil {
    private val handler = Handler(Looper.getMainLooper())

    fun runOnUiThread(action: () -> Unit) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(action)
        } else {
            action.invoke()
        }
    }
}