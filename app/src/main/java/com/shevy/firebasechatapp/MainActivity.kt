package com.shevy.firebasechatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.*
import androidx.core.widget.addTextChangedListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            messageEditText.text = ""
        }
        sendImageButton.setOnClickListener {

        }

    }

}