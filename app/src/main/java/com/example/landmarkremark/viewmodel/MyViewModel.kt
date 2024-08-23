package com.example.landmarkremark.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.landmarkremark.common.Common
import com.example.landmarkremark.model.Notes
import com.example.landmarkremark.model.User
import com.google.firebase.firestore.FirebaseFirestore

class MyViewModel : ViewModel() {
    var user = MutableLiveData<User>()
    var mutableLiveDataAllNotes = MutableLiveData<List<Notes>>()
    var mutableLiveDataSearchNotes = MutableLiveData<List<Notes>>()
    var mutableLiveDataMyNotes = MutableLiveData<List<Notes>>()

    init {
        // Thiết lập lắng nghe thay đổi trong Firestore
        val firestore = FirebaseFirestore.getInstance()
        val notesCollection = firestore.collection(Common.NOTES)

        notesCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val notesList = mutableListOf<Notes>()
                for (doc in snapshots.documents) {
                    val note = doc.toObject(Notes::class.java)
                    if (note != null) {
                        notesList.add(note)
                    }
                }
                mutableLiveDataAllNotes.value = notesList
            }
        }
    }

    fun getMyNote() {
        val firestore = FirebaseFirestore.getInstance()
        val myNoteCollection = firestore.collection(Common.NOTES).whereEqualTo("id", user.value?.id)
        myNoteCollection.addSnapshotListener { snapshots, e ->

            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val myNotesList = mutableListOf<Notes>()
                for (doc in snapshots.documents) {
                    val note = doc.toObject(Notes::class.java)
                    if (note != null) {
                        myNotesList.add(note)
                    }
                }
                mutableLiveDataMyNotes.value = myNotesList
            }
        }
    }
}
