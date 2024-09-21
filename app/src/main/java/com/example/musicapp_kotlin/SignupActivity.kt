package com.example.musicapp_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp_kotlin.databinding.ActivitySignupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.imageView3.setOnClickListener {
            signInWithGoogle()
        }

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

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Đăng nhập bằng Google thành công, xác thực với Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Đăng nhập bằng Google thất bại
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        setInProgress(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                setInProgress(false)
                if (task.isSuccessful) {
                    // Đăng ký/đăng nhập thành công, chuyển đến MainActivity
                    Toast.makeText(this, "Đăng nhập bằng Google thành công", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Đăng nhập thất bại
                    Toast.makeText(this, "Xác thực thất bại.", Toast.LENGTH_SHORT).show()
                }
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