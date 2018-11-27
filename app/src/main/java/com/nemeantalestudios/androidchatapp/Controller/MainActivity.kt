package com.nemeantalestudios.androidchatapp.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.nemeantalestudios.androidchatapp.R
import com.nemeantalestudios.androidchatapp.Service.AuthService
import com.nemeantalestudios.androidchatapp.Service.UserDataService
import com.nemeantalestudios.androidchatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import com.nemeantalestudios.androidchatapp.Utilities.Colors
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {
                navHeaderUsername.text = UserDataService.name
                navHeaderEmail.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                navHeaderAvatar.setImageResource(resourceId)
                navHeaderAvatar.setBackgroundColor(Colors.formatColorFromIOStoAndroidFormat(UserDataService.avatarColor))
                loginBtnNavHeader.setText(R.string.logout)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver, IntentFilter(
                BROADCAST_USER_DATA_CHANGE
            )
        )

    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
        if (AuthService.isLoggedIn) {
            UserDataService.clearUserData()
            AuthService.clearUserData()
            resetLogoutUI()

        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }



    }

    fun addChannelButton(view: View) {
        print("addChannelButton clicked")
    }

    fun sendMessageBtnClicked(view: View) {
        print("sendMessageBtnClicked clicked")

    }

}
