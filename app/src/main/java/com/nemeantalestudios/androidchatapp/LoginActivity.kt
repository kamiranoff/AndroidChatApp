package com.nemeantalestudios.androidchatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    fun loginCreateUserBtnClicked(view: View) {
        val signupActivity = Intent(this, SignupActivity::class.java)
        startActivity(signupActivity)

    }

    fun loginBtnClicked(view: View) {

    }
}
