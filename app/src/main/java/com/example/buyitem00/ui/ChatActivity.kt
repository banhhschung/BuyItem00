package com.example.buyitem00.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.buyitem00.MainActivity
import com.example.buyitem00.R
import com.example.buyitem00.cloudmessagingservice.FirebaseService
import com.example.buyitem00.databinding.ActivityChatBinding
import com.google.firebase.messaging.FirebaseMessaging

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment

    }
}