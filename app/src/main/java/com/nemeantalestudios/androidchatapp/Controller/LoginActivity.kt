package com.nemeantalestudios.androidchatapp.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nemeantalestudios.androidchatapp.R
import com.nemeantalestudios.androidchatapp.Service.AuthService
import com.nemeantalestudios.androidchatapp.Service.UserDataService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    fun loginCreateUserBtnClicked(view: View) {
        val signupActivity = Intent(this, SignupActivity::class.java)
        startActivity(signupActivity)
        finish()

    }

    fun loginBtnClicked(view: View) {
        val email = loginEmailText.text.toString()
        val password = passwordLoginText.text.toString()

        AuthService.loginUser(this, email, password) { loginSuccess ->
            if (loginSuccess) {
                UserDataService.findUserByMail(this) { findByEmailSuccess ->
                    finish()
                }
            }
        }
    }
}
