package com.nemeantalestudios.androidchatapp.Service

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nemeantalestudios.androidchatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import com.nemeantalestudios.androidchatapp.Utilities.URL_CREATE_USER
import com.nemeantalestudios.androidchatapp.Utilities.URL_GET_USER_BY_EMAIL
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object UserDataService {

    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun createUser(
        context: Context,
        name: String,
        email: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()


        val createRequest = object : JsonObjectRequest(
            Method.POST, URL_CREATE_USER, null,
            Response.Listener { response ->

                try {
                    this.email = response.getString("email")
                    this.name = response.getString("name")
                    this.avatarName = response.getString("avatarName")
                    this.avatarColor = response.getString("avatarColor")
                    this.id = response.getString("_id")

                    complete(true)

                } catch (error: JSONException) {
                    Log.e("JSON", "could not getString from response: ${error.localizedMessage}")
                    complete(false)
                }


            },
            Response.ErrorListener { error ->
                Log.e("ERROR", "Could not create user : ${error}")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=UTF-8"
            }


            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${AuthService.authToken}")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(createRequest)
    }


    fun findUserByMail(context: Context, complete: (Boolean) -> Unit) {
        val findUserRequest = object : JsonObjectRequest(
            Method.GET,
            "$URL_GET_USER_BY_EMAIL/${AuthService.userEmail}",
            null,
            Response.Listener { response ->
                try {
                    name = response.getString("name")
                    email = response.getString("email")
                    avatarName = response.getString("avatarName")
                    avatarColor = response.getString("avatarColor")
                    id = response.getString("_id")


                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                    complete(true)

                } catch (e: JSONException) {
                    Log.d("JSON", "ERROR -> Could not parse json: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "Could not find user: $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=UTF-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${AuthService.authToken}")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(findUserRequest)

    }


    fun clearUserData() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
    }
}