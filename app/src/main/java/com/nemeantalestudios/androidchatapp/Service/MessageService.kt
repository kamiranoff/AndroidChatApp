package com.nemeantalestudios.androidchatapp.Service

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.nemeantalestudios.androidchatapp.Controller.App
import com.nemeantalestudios.androidchatapp.Model.Channel
import com.nemeantalestudios.androidchatapp.Model.Message
import com.nemeantalestudios.androidchatapp.Utilities.URL_GET_CHANNELS
import com.nemeantalestudios.androidchatapp.Utilities.URL_GET_MESSAGES_BY_CHANNEL_ID
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getMessagesByChannel(id: String, complete: (Boolean) -> Unit) {
        val messagesRequest = object : JsonArrayRequest(Method.GET, "$URL_GET_MESSAGES_BY_CHANNEL_ID$id", null,
            Response.Listener {
                response ->
                try {
                    for(x in 0 until response.length()) {

                        val responseAt = response.getJSONObject(x)

                        val messageBody = responseAt.getString("messageBody")
                        val username = responseAt.getString("userName")
                        val channelId = responseAt.getString("channelId")
                        val avatar = responseAt.getString("userAvatar")
                        val avatarColor = responseAt.getString("userAvatarColor")
                        val messageId = responseAt.getString("_id")
                        val timeStamp = responseAt.getString("timeStamp")

                        val message = Message(messageBody, username, channelId, avatar, avatarColor, messageId, timeStamp)
                        this.messages.add(message)
                    }
                    complete(true)
                } catch (error: JSONException) {
                    Log.d("ERROR", "Could not parse messages")
                    complete(false)
                }
            },
            Response.ErrorListener {
                Log.d("ERROR", "Could not retrieve messages")
                complete(false)
            })
            {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                    return headers
                }
            }

        App.sharedPreferences.requestQueue.add(messagesRequest)
    }


    fun clearMessages() {
        messages.clear()
    }

    fun getChannels(complete: (Boolean) -> Unit) {

        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
            Response.Listener { response ->

                try {

                    for (x in 0 until response.length()) {
                        val channel = response.getJSONObject(x)
                        val name = channel.getString("name")
                        val description = channel.getString("description")
                        val id = channel.getString("_id")

                        val newChannel = Channel(name, description, id)
                        this.channels.add(newChannel)
                    }
                    complete(true)

                } catch (e: JSONException) {
                    complete(false)
                }

            },
            Response.ErrorListener {
                Log.d("ERROR", "Could not retrieve channels")
                complete(false)
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(channelsRequest)
    }

    fun clearChannels() {
        channels.clear()
    }

    fun clearAll() {
        clearChannels()
        clearMessages()
    }
}
