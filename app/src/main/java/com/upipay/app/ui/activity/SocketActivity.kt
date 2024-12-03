package com.upipay.app.ui.activity

import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import com.upipay.app.R


import io.socket.client.IO
import io.socket.client.Socket


class SocketActivity : AppCompatActivity() {
    private lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_socket)


        val serverUrl = "http://192.168.1.7:3000" // Replace with your server's IP or localhost
        mSocket = IO.socket(serverUrl)
        mSocket.connect()
        Log.d("SocketIO", "$serverUrl  "+mSocket)
        mSocket.on(Socket.EVENT_CONNECT) {
            Log.d("SocketIO", "Connected")
        }
    }
}