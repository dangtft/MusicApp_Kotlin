package com.example.musicapp_kotlin

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp_kotlin.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createAccount.setOnClickListener{

            val email = binding.emailEdittext.text.toString().trim()
            val password = binding.passwordEdittext.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEdittext.text.toString().trim()

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailEdittext.setError("Invalid email")
                return@setOnClickListener
            }else {
                binding.emailEdittext.error = null
            }

            if(password.length < 6){
                binding.passwordEdittext.setError("Password should be at least 6 char")
                return@setOnClickListener
            }else {
                binding.passwordEdittext.error = null
            }

            if (!password.equals(confirmPassword)){
                binding.confirmPasswordEdittext.setError("Password not matched")
                return@setOnClickListener
            }else {
                binding.confirmPasswordEdittext.error = null
            }

            createAccountWithFirebase(email,password)
        }
        binding.gotoLoginBtn.setOnClickListener{
            finish()
        }
    }

    fun createAccountWithFirebase(email:String,password : String){
        setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                setInProgress(false)
                Toast.makeText(applicationContext,"User created successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener{
                setInProgress(false)
                Toast.makeText(applicationContext,"Create account failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun setInProgress(inProgress: Boolean){
        if(inProgress){
            binding.createAccount.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.createAccount.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

}