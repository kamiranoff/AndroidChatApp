package com.nemeantalestudios.androidchatapp.Model

class Channel constructor(val name: String, val description: String, val id:String) {
    fun totString():String {
        return "$name"
    }
 }