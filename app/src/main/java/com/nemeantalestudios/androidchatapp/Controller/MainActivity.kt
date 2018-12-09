package com.nemeantalestudios.androidchatapp.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.nemeantalestudios.androidchatapp.Model.Channel
import com.nemeantalestudios.androidchatapp.Model.Message
import com.nemeantalestudios.androidchatapp.R
import com.nemeantalestudios.androidchatapp.Service.AuthService
import com.nemeantalestudios.androidchatapp.Service.MessageService
import com.nemeantalestudios.androidchatapp.Service.UserDataService
import com.nemeantalestudios.androidchatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import com.nemeantalestudios.androidchatapp.Utilities.Colors
import com.nemeantalestudios.androidchatapp.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapters()


        if (App.sharedPreferences.isLoggedIn) {
            UserDataService.findUserByMail(this) {}
        }

        channel_list.setOnItemClickListener { _, _, i, _ ->
            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()

        }

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager
            .getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }


    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.sharedPreferences.isLoggedIn) {
                navHeaderUsername.text = UserDataService.name
                navHeaderEmail.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                navHeaderAvatar.setImageResource(resourceId)
                navHeaderAvatar.setBackgroundColor(Colors.formatColorFromIOStoAndroidFormat(UserDataService.avatarColor))
                loginBtnNavHeader.setText(R.string.logout)
                MessageService.getChannels { complete ->
                    if (complete) {
                        if (MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()

                        }
                    }
                }
            }

        }
    }


    fun resetLogoutUI() {
        navHeaderUsername.setText(R.string.login)
        navHeaderEmail.text = ""
        navHeaderAvatar.setImageResource(R.drawable.profiledefault)
        navHeaderAvatar.setBackgroundColor(Color.TRANSPARENT)
        loginBtnNavHeader.setText(R.string.login)
    }

    fun loginBtnNavClicked(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
            UserDataService.clearUserData()
            AuthService.clearUserData()
            MessageService.clearAll()
            resetLogoutUI()

        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }


    }

    fun addChannelButton(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder
                .setView(dialogView)
                .setPositiveButton("Add") { dialog: DialogInterface?, which: Int ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelField)
                    val descriptionTextField = dialogView.findViewById<EditText>(R.id.addChannelDescripttionField)
                    val channelName = nameTextField.text.toString()
                    val channelDescription = descriptionTextField.text.toString()

                    socket.emit("newChannel", channelName, channelDescription)

                }
                .setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->

                }
                .show()

        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "${selectedChannel?.name}"

        if(selectedChannel != null) {
            MessageService.getMessagesByChannel(selectedChannel!!.id) {
                complete ->
                if(complete) {
                    for (message in MessageService.messages) {
                        println(message.text)
                    }
                }
            }
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if(App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String


                val channel = Channel(channelName, channelDescription, channelId)
                MessageService.channels.add(channel)
                channelAdapter.notifyDataSetChanged()
            }
        }

    }

    private val onNewMessage = Emitter.Listener { args ->

        if(App.sharedPreferences.isLoggedIn && selectedChannel != null) {
            runOnUiThread {
                val channelId = args[2] as String
                if(channelId == selectedChannel!!.id) {
                    val msgBody = args[0] as String
                    val username = args[3] as String
                    val avatar = args[4] as String
                    val avatarColor = args[5] as String
                    val messageId = args[6] as String
                    val timestamp = args[7] as String

                    val message = Message(msgBody, username, channelId, avatar, avatarColor, messageId, timestamp)
                    MessageService.messages.add(message)
                }
            }
        }

    }

    fun sendMessageBtnClicked(view: View) {
        print("sendMessageBtnClicked clicked")
        if (App.sharedPreferences.isLoggedIn && sendMessageText.text.isNotEmpty() && selectedChannel !== null) {
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id

            socket.emit(
                "newMessage",
                sendMessageText.text.toString(),
                userId,
                channelId,
                UserDataService.name,
                UserDataService.avatarName,
                UserDataService.avatarColor
            )
            sendMessageText.text.clear()
            hideKeyboard()
        }
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
