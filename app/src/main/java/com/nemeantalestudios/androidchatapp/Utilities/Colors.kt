package com.nemeantalestudios.androidchatapp.Utilities

import android.graphics.Color
import android.util.Log
import com.nemeantalestudios.androidchatapp.Service.UserDataService
import java.util.*

object Colors {
    fun formatColorFromAndroidtoIOSFormat(r: Int, g: Int, b: Int): String {


        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        UserDataService.avatarColor = "[$savedR, $savedG, $savedB, 1]"

        return UserDataService.avatarColor;

    }

    fun formatColorFromIOStoAndroidFormat(components: String): Int {
        // from "[0.XXX, 0.XXX, 0.XXX, 1]" to RGB

        val strippedColor = components
            .replace("[", "")
            .replace("]", "")
            .replace(",", " ")


        // to RGB values.
        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()) {
            try {
                r = (scanner.nextDouble() * 255).toInt()
                g = (scanner.nextDouble() * 255).toInt()
                b = (scanner.nextDouble() * 255).toInt()

            } catch (e: InputMismatchException) {
                Log.e("ERROR", "Scanner error,: $e")
            }
        }

        return Color.rgb(r, g, b)
    }
}