package com.nemeantalestudios.androidchatapp.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.nemeantalestudios.androidchatapp.R
import com.nemeantalestudios.androidchatapp.Service.AuthService
import com.nemeantalestudios.androidchatapp.Service.UserDataService
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    var avatarColor = "[0.5,0.5,0.5,1]"
    var avatarName = "profileDefault"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun onChooseAvatarClicked(view: View) {
        val random = Random()
        val avatarNumber = random.nextInt(100) + 1
        avatarName = "heroes$avatarNumber"
        val resourceId = resources.getIdentifier(avatarName, "drawable", packageName)
        chooseAvatarImage.setImageResource(resourceId);
    }

    fun convertToIOSColor(r: Int, g: Int, b: Int): String {


        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"

        return avatarColor;

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
        convertToIOSColor(r, g, b);
    }


    fun onSignupUserClicked(view: View) {
        val username = signupUsernameField.text.toString()
        val email = signupEmailField.text.toString()
        val password = signupPasswordField.text.toString()


        if (email == "" || password == "") {
            Toast.makeText(this, "Please fill email and password fields", Toast.LENGTH_SHORT).show()
        }

        AuthService.registerUser(this, email, password) { registerSuccess ->
            if (registerSuccess) {
                println("Psylocke created")
                AuthService.loginUser(this, email, password) { loginSuccess ->
                    if(loginSuccess) {
                        println("User logged in: ${AuthService.userEmail}")
                        UserDataService.createUser(this,username,email, avatarName, avatarColor) { createUserSuccess ->
                            if(createUserSuccess) {
                               finish()
                            }
                        }

                    }
                }
            }
        }
    }

}
