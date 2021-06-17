package com.example.mvvm_koin_firebase_kotlin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), UserAdapter.OnItemClickListener,
    CoroutineScope by MainScope() {
    lateinit var databaseReference: DatabaseReference
    lateinit var nameEditText: EditText
    lateinit var designEditText: EditText
    lateinit var saveBtn: Button
    lateinit var list: ArrayList<UserModel>
    lateinit var userAdapter: UserAdapter
    lateinit var recycler: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler = findViewById(R.id.recyclerID)
        list = ArrayList()
        userAdapter= UserAdapter(list)
        databaseReference = FirebaseDatabase.getInstance().reference
        saveBtn = findViewById(R.id.saveBtnID)
        nameEditText = findViewById(R.id.nameEditTextID)
        designEditText = findViewById(R.id.designationEditTextID)
        recycler.adapter = userAdapter
        recycler.addItemDecoration(
            DividerItemDecoration(
                this@MainActivity,
                LinearLayoutManager.VERTICAL
            )
        )
        recycler.setHasFixedSize(true)
        // add value to firebase database
        saveBtn.setOnClickListener {
            val userName = nameEditText.text.toString().trim()
            val userDesignation = designEditText.text.toString().trim()
            if (userName.isNotEmpty() && userDesignation.isNotEmpty()) {
                val userID = databaseReference.push().key
                val user = userID?.let { it1 -> UserModel(it1, userName, userDesignation) }
                databaseReference.child(userID!!).setValue(user).addOnCompleteListener {
                    Log.d("StatusAdd", "User Add Successfully")
                }
            } else {
                Log.d("StatusAdd", "User Failed To add")
            }

        }
        CoroutineScope(Dispatchers.IO).launch {
            fetChData()
        }


    }

    private fun fetChData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (user in snapshot.children) {
                        val getUser = user.getValue(UserModel::class.java)
                        list.add(getUser!!)
                    }
                    launch(Dispatchers.Main) {
                       // userAdapter.addUserList(list)
                        userAdapter.notifyDataSetChanged()
                    }

                    Log.d("StatusReterived", "User get To add")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("StatusReterived", "User Failed To add")
            }

        })

    }

    override fun itemClick(view: View, position: Int) {
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.drop_down_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.updateID -> updateUser(position)
                R.id.deleteID -> deleteUser(position)
                else -> false
            }
            true
        }

    }

    private fun deleteUser(position: Int) {
        databaseReference.child(userAdapter.dataList[position].id).removeValue()
    }

    private fun updateUser(position: Int) {
        val viewLayout = LayoutInflater.from(this).inflate(R.layout.update_user, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(viewLayout)
        val updateBtn = deleteDialog.findViewById<Button>(R.id.updateUserBtnID)
        val cancelBtn = deleteDialog.findViewById<Button>(R.id.cancelBtnID)
        var nameEditText = deleteDialog.findViewById<EditText>(R.id.updateNameID)
        var designEditText = deleteDialog.findViewById<EditText>(R.id.updateDesignationID)
        deleteDialog.show()
        updateBtn!!.setOnClickListener {
            val name = nameEditText?.text.toString().trim()
            val designation = designEditText?.text.toString().trim()
            if (name.isNotEmpty() && designation.isNotEmpty()) {
                val dbRef = FirebaseDatabase.getInstance().getReference("user")
                val user = UserModel(userAdapter.dataList[position].id, name, designation)
                dbRef.child(userAdapter.dataList[position].id).setValue(user)
                deleteDialog.dismiss()

            }
        }
        cancelBtn!!.setOnClickListener {
            deleteDialog.dismiss()
        }


    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}