package com.example.musicapp_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp_kotlin.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Retrieve user information from Firebase Authentication
            val email = currentUser.email
            val name = currentUser.displayName

            // Update UI elements
            findViewById<TextView>(R.id.user_mail).text = email
            findViewById<TextView>(R.id.user_name).text = name ?: "Name not available"
        }
        findViewById<Button>(R.id.btnChangePassword).setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }

}