package com.nemeantalestudios.androidchatapp.Controller
import android.app.Application
import com.nemeantalestudios.androidchatapp.Utilities.SharedPrefs

class App : Application() {

    companion object {
        lateinit var sharedPreferences: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = SharedPrefs(applicationContext)
    }
}
