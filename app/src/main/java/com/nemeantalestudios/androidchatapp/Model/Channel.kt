package com.nemeantalestudios.androidchatapp.Model

class Channel constructor(val name: String, val description: String, val id:String) {
    override fun toString():String {
        return "#$name"
    }
 }