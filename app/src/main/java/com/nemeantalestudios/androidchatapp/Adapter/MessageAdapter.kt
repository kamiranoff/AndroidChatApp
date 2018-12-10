package com.nemeantalestudios.androidchatapp.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nemeantalestudios.androidchatapp.Model.Message
import com.nemeantalestudios.androidchatapp.R
import com.nemeantalestudios.androidchatapp.Utilities.Colors.formatColorFromIOStoAndroidFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(val context: Context, val messages: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(context, messages[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView.findViewById<ImageView>(R.id.messageUserAvatar)
        val timeStamp = itemView.findViewById<TextView>(R.id.messageTimeStamp)
        val username = itemView.findViewById<TextView>(R.id.messageUsername)
        val messageBody = itemView.findViewById<TextView>(R.id.messageBody)

        fun bindMessage(context: Context, message: Message) {
            val resourceId = context.resources.getIdentifier(message.avatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(formatColorFromIOStoAndroidFormat(message.avatarColor))
            username.text = message.username
            timeStamp.text = returnDateString(message.timestamp)
            messageBody.text = message.messageBody
        }

        fun returnDateString(isoString: String): String {

            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var convertedDate = Date()
            try {
                convertedDate = isoFormatter.parse(isoString)

            } catch (error: ParseException) {
                Log.d("ERROR", "Cannot parse date")
            }

            return SimpleDateFormat("E, h:mm a", Locale.getDefault()).format(convertedDate)
        }
    }
}