package com.shevy.firebasechatapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.shevy.firebasechatapp.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private val RC_IMAGE_PICKER = 123

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
    lateinit var usersDatabaseReference: DatabaseReference
    lateinit var usersChildEventListener: ChildEventListener
    lateinit var storage: FirebaseStorage
    lateinit var chatImageStorageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database
        storage = Firebase.storage

        messagesDatabaseReference = database.reference.child("messages")
        usersDatabaseReference = database.reference.child("users")
        chatImageStorageReference = storage.reference.child("chat_images")

/*         messagesDatabaseReference.child("message1").setValue("Hello Firebase")
        messagesDatabaseReference.child("message2").setValue("Hello world")
        usersDatabaseReference.child("user1").setValue("Joe")*/

        userName = intent.getStringExtra("userName") ?: "Default User"

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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.apply {
                type = "image/*"
                putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            }

/*            if(intent.resolveActivity(packageManager) != null) {
                startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER)
            }*/
            startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER)
        }

        usersChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user: User? = snapshot.getValue(User::class.java)

                if (user?.id == FirebaseAuth.getInstance().currentUser?.uid) {
                    userName = user?.name.toString()
                }
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
        usersDatabaseReference.addChildEventListener(usersChildEventListener)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            val imageReference = chatImageStorageReference
                .child(selectedImageUri?.lastPathSegment.toString())

            val uploadTask = selectedImageUri?.let { imageReference.putFile(it) }

            uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageReference.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val message = AwesomeMessage()
                    message.apply {
                        imageUrl = downloadUri.toString()
                        name = userName
                        messagesDatabaseReference.push().setValue(message)
                    }
                } else {

                }
            }
        }
    }
}