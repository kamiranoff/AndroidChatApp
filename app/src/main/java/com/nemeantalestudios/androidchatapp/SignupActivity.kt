package com.nemeantalestudios.androidchatapp

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    var avatarColor = "[0.5,0.5,0.5,1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun onChooseAvatarClicked(view: View) {
        val random = Random()
        val avatarNumber = random.nextInt(100) + 1
        val avatarName = "heroes$avatarNumber"
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

    }

}
