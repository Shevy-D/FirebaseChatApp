package com.shevy.firebasechatapp

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.shevy.firebasechatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private lateinit var messageListView: ListView
    private lateinit var adapter: AwesomeMessageAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var sendImageButton: ImageButton
    private lateinit var sendMessageButton: Button
    private lateinit var messageEditText: TextView

    private lateinit var userName: String

    lateinit var database: FirebaseDatabase
    lateinit var messagesDatabaseReference: DatabaseReference
    lateinit var messagesChildEventListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database
        messagesDatabaseReference = database.reference.child("messages")

/*         messagesDatabaseReference.child("message1").setValue("Hello Firebase")
        messagesDatabaseReference.child("message2").setValue("Hello world")
        usersDatabaseReference.child("user1").setValue("Joe")*/

        userName = "Default User"
        progressBar = binding.progressBar
        sendImageButton = binding.sendPhotoButton
        sendMessageButton = binding.sendMessageButton
        messageEditText = binding.messageEditText

        val awesomeMessage = ArrayList<AwesomeMessage>()
        messageListView = binding.messageListView
        adapter = AwesomeMessageAdapter(this, R.layout.message_item, awesomeMessage)
        messageListView.adapter = adapter

        progressBar.visibility = ProgressBar.INVISIBLE

        messageEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    sendMessageButton.isEnabled = p0.toString().trim().isNotEmpty()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(500))
        }
        sendMessageButton.setOnClickListener {
            val message: AwesomeMessage = AwesomeMessage()
            message.apply {
                text = messageEditText.text.toString()
                name = userName
                imageUrl = null
            }
            messagesDatabaseReference.push().setValue(message)

            messageEditText.text = ""
        }
        sendImageButton.setOnClickListener {

        }

        messagesChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message: AwesomeMessage? = snapshot.getValue(AwesomeMessage::class.java)

                adapter.add(message)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        messagesDatabaseReference.addChildEventListener(messagesChildEventListener)
    }

}