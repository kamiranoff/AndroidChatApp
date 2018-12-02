package com.nemeantalestudios.androidchatapp.Controller

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.nemeantalestudios.androidchatapp.R
import com.nemeantalestudios.androidchatapp.Service.AuthService
import com.nemeantalestudios.androidchatapp.Service.UserDataService
import com.nemeantalestudios.androidchatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import com.nemeantalestudios.androidchatapp.Utilities.Colors
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    var avatarColor = "[0.5,0.5,0.5,1]"
    var avatarName = "profileDefault"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        signupUserSpinner.visibility = View.INVISIBLE
    }

    fun onChooseAvatarClicked(view: View) {
        val random = Random()
        val avatarNumber = random.nextInt(100) + 1
        avatarName = "heroes$avatarNumber"
        val resourceId = resources.getIdentifier(avatarName, "drawable", packageName)
        chooseAvatarImage.setImageResource(resourceId);
    }


    fun onRemoveBgColor(view: View) {
        chooseAvatarImage.setBackgroundColor(0x00000000)
    }

    fun onGenerateBgColorClicked(view: View) {
        val random = Random()
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)

        chooseAvatarImage.setBackgroundColor(Color.rgb(r, g, b))
        Colors.formatColorFromAndroidtoIOSFormat(r, g, b);
    }


    fun onSignupUserClicked(view: View) {
        val username = signupUsernameField.text.toString()
        val email = signupEmailField.text.toString()
        val password = signupPasswordField.text.toString()


        if (email.isEmpty() || password.isEmpty() && username.isEmpty()) {
            return Toast.makeText(this, "Please fill email and password fields", Toast.LENGTH_SHORT).show()
        }

        hideKeyboard()
        enableProgressBar(true)
        AuthService.registerUser(this, email, password) { registerSuccess ->
            if (registerSuccess) {
                AuthService.loginUser(this, email, password) { loginSuccess ->
                    if (loginSuccess) {
                        println("User logged in: ${AuthService.userEmail}")
                        UserDataService.createUser(
                            this,
                            username,
                            email,
                            avatarName,
                            avatarColor
                        ) { createUserSuccess ->
                            if (createUserSuccess) {

                                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                enableProgressBar(false)
                                finish()
                            } else {
                                errorToast("Sorry could not create your user")
                            }
                        }

                    } else {
                        errorToast("Sorry could not log in your user")
                    }
                }
            } else {
                errorToast("Sorry could not signup your user")
            }
        }
    }

    fun errorToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        enableProgressBar(false)
    }


    fun enableProgressBar(enable: Boolean) {
        if (enable) {
            signupUserSpinner.visibility = View.VISIBLE
        } else {
            signupUserSpinner.visibility = View.INVISIBLE
        }
        signupUserBtn.isEnabled = !enable
        generateBgColorBtn.isEnabled = !enable
        chooseAvatarImage.isEnabled = !enable

    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
