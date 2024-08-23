package com.example.landmarkremark

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.landmarkremark.common.Common
import com.example.landmarkremark.model.User
import com.example.landmarkremark.viewmodel.MyViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    private val myViewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth && Firestore
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if (currentUser != null) {
            checkUserFromFirebase(currentUser)
        } else {
            showLoginFragment()
        }
    }

    private fun checkUserFromFirebase(firebaseUser: FirebaseUser) {
        db.collection(Common.USERS)
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.id == firebaseUser.uid && document.data != null) {
                    myViewModel.user.value = (document.toObject<User>()!!)
                    transitToMapsActivity()
                }
            }
            .addOnFailureListener { exception ->
                showLoginFragment()
            }
    }

    private fun showLoginFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, LoginFragment.newInstance(), "LoginFragment")
            .commit()
    }

    fun transitToMapsActivity() {
        Common.currentUser = myViewModel.user.value
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}