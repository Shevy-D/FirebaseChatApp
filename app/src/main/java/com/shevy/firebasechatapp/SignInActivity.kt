package com.shevy.firebasechatapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.shevy.firebasechatapp.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {
    private val TAG = "SignInActivity"

    lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    private val usersDatabaseReference: DatabaseReference? = null

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var toggleLoginSignUpTextView: TextView
    private lateinit var loginSignUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        nameEditText = binding.nameEditText
        toggleLoginSignUpTextView = binding.toggleLoginSignUpTextView
        loginSignUpButton = binding.loginSignUpButton

        loginSignUpButton.setOnClickListener {
            loginSignUpUser(
                emailEditText.text.toString().trim(),
                passwordEditText.text.toString().trim()
            )
        }
    }

    private fun loginSignUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@SignInActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }
    }
}