package com.shevy.firebasechatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.shevy.firebasechatapp.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {
    private val TAG = "SignInActivity"

    lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var toggleLoginSignUpTextView: TextView
    private lateinit var loginSignUpButton: Button
    private var loginModeIsActive: Boolean = false

    lateinit var database: FirebaseDatabase
    lateinit var usersDatabaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database
        usersDatabaseReference = database.reference.child("users")


        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        repeatPasswordEditText = binding.repeatPasswordEditText
        nameEditText = binding.nameEditText
        toggleLoginSignUpTextView = binding.toggleLoginSignUpTextView
        loginSignUpButton = binding.loginSignUpButton

        loginSignUpButton.setOnClickListener {
            loginSignUpUser(
                emailEditText.text.toString().trim(),
                passwordEditText.text.toString().trim()
            )
        }

        toggleLoginSignUpTextView.setOnClickListener {
            if (loginModeIsActive) {
                repeatPasswordEditText.visibility = View.VISIBLE
                loginModeIsActive = false
                loginSignUpButton.text = "Sign Up"
                toggleLoginSignUpTextView.text = "Tap to log in"
            } else {
                repeatPasswordEditText.visibility = View.GONE
                loginModeIsActive = true
                loginSignUpButton.text = "Log In"
                toggleLoginSignUpTextView.text = "Tap to sign up"
            }
        }

        if (auth.currentUser != null) {
            startActivity(Intent(this, UserListActivity::class.java))
        }
    }

    private fun loginSignUpUser(email: String, password: String) {
        if (loginModeIsActive) {
            if (password.length < 6) {
                Toast.makeText(
                    this, "Passwords must be at least 7 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email == "") {
                Toast.makeText(
                    this, "Please input your email",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            //updateUI(user)

                            val intent = Intent(this, UserListActivity::class.java)
                            intent.putExtra("userName", nameEditText.text.toString().trim())
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                this, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            //updateUI(null)
                        }
                    }
            }
        } else {
            if (password != repeatPasswordEditText.text.toString().trim()) {
                Toast.makeText(
                    this, "Passwords don't match",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 6) {
                Toast.makeText(
                    this, "Passwords must be at least 7 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email == "") {
                Toast.makeText(
                    this, "Please input your email",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            user?.let { createUser(it) }
                            //updateUI(user)

                            val intent = Intent(this, UserListActivity::class.java)
                            intent.putExtra("userName", nameEditText.text.toString().trim())
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                this, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            //updateUI(null)
                        }
                    }
            }
        }
    }

    private fun createUser(firebaseUser: FirebaseUser) {
        val user = User()
        user.id = firebaseUser.uid
        user.email = firebaseUser.email
        user.name = nameEditText.text.toString().trim()

        usersDatabaseReference.push().setValue(user)
    }
}