package com.shevy.firebasechatapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.shevy.firebasechatapp.databinding.ActivityUserListBinding


class UserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding

    private lateinit var userDatabaseReference: DatabaseReference
    private var userChildEventListener: ChildEventListener? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var userName: String
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        userName = intent.getStringExtra("userName").toString()
        userArrayList = ArrayList()

        buildRecyclerView()
        attachUserDatabaseReferenceListener()
    }

    private fun attachUserDatabaseReferenceListener() {
        userDatabaseReference = Firebase.database.reference.child("users")
        if (userChildEventListener == null) {
            userChildEventListener = object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)

                    if (user?.id != auth.currentUser?.uid) {
                        user?.avatarMockUpResource = R.drawable.ic_baseline_person_24
                        user?.let { userArrayList.add(it) }
                        userAdapter.notifyDataSetChanged()
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
            userDatabaseReference.addChildEventListener(userChildEventListener as ChildEventListener)
        }
    }

    private fun buildRecyclerView() {
        //userRecyclerView = binding.userListRecyclerView
/*        userAdapter = UserAdapter(userArrayList, object: UserAdapter.OnUserClickListener {
            override fun onUserClick(position: Int) {
                getToChat(position)
            }
        })*/
        binding.userListRecyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration( DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@UserListActivity)
            userAdapter = UserAdapter(userArrayList, object : UserAdapter.OnUserClickListener {
                override fun onUserClick(position: Int) {
                    goToChat(position)
                }
            })
            adapter = userAdapter
        }
    }

    private fun goToChat(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("recipientUserId", userArrayList[position].id)
        intent.putExtra("recipientUserName", userArrayList[position].name)
        intent.putExtra("userName", userName)
        startActivity(intent)
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
}