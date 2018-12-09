package com.nemeantalestudios.androidchatapp.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.nemeantalestudios.androidchatapp.R
import com.nemeantalestudios.androidchatapp.Service.AuthService
import com.nemeantalestudios.androidchatapp.Service.UserDataService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableProgressBar(false)
    }

    fun loginCreateUserBtnClicked(view: View) {
        val signupActivity = Intent(this, SignupActivity::class.java)
        startActivity(signupActivity)
        finish()

    }

    fun loginBtnClicked(view: View) {
        val email = loginEmailText.text.toString()
        val password = passwordLoginText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill email and password", Toast.LENGTH_LONG).show()
            return
        }


        enableProgressBar(true)

        AuthService.loginUser( email, password) { loginSuccess ->
            if (loginSuccess) {
                UserDataService.findUserByMail(this) { findByEmailSuccess ->
                    if (findByEmailSuccess) {
                        enableProgressBar(false)
                        finish()
                    } else {
                        errorToast("Failed to find your account")
                    }
                }
            } else {
                errorToast("Failed to login")
            }
        }
    }

    fun errorToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        enableProgressBar(false)
    }


    fun enableProgressBar(enable: Boolean) {
        if (enable) {
            loginActivityProgressBar.visibility = View.VISIBLE
        } else {
            loginActivityProgressBar.visibility = View.INVISIBLE
        }
        loginBtn.isEnabled = !enable
        loginCreateUserBtn.isEnabled = !enable
    }
}
