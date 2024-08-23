package com.example.landmarkremark.allnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landmarkremark.MapsActivity
import com.example.landmarkremark.common.Common
import com.example.landmarkremark.databinding.FragmentNoteLocationBinding
import com.example.landmarkremark.model.Notes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val LAT = "lat"
private const val LNG = "lng"

class NoteLocationDialogFragment : BottomSheetDialogFragment() {
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private lateinit var binding : FragmentNoteLocationBinding
    private lateinit var noteItemAdapter: NotesAdapter
    private var listNotesLocation = mutableListOf<Notes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lat = it.getDouble(LAT)
            lng = it.getDouble(LNG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteLocationBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet  = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = (resources.displayMetrics.heightPixels * 0.4).toInt()
            it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.95).toInt()
        }

        getDataListNotes()

        binding.locationTxt.text = "Lat : $lat - Lng : $lng"

        val recyclerView = binding.allNoteRecyclerview
        noteItemAdapter = NotesAdapter(listNotesLocation)
        recyclerView.apply {
            adapter = noteItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))
        }

        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        binding.addNoteBtn.setOnClickListener {
            (requireActivity() as MapsActivity).showAddNoteDialogFragment(lat, lng)
        }
    }

    private fun getDataListNotes() =
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val documents = getDocumentsFromFireStore()
                for (document in documents) {
                    listNotesLocation.add(document.toObject(Notes::class.java)!!)
                }
                noteItemAdapter.notifyDataSetChanged()
            } catch (exception: Exception) {
                Toast.makeText(requireContext(), "getDataMyNotes() $exception", Toast.LENGTH_SHORT).show()
            }
        }

    private suspend fun getDocumentsFromFireStore(): List<DocumentSnapshot> =
        suspendCancellableCoroutine { continuation ->
            val mapsActivity = requireActivity() as MapsActivity
            mapsActivity.db.collection(Common.NOTES)
                .whereEqualTo("currentLocation", listOf(lat, lng))
                .get()
                .addOnSuccessListener { querySnapshot ->
                    continuation.resume(querySnapshot.documents)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    companion object {
        @JvmStatic fun newInstance(lat: Double, lng: Double) =
            NoteLocationDialogFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LAT, lat)
                    putDouble(LNG, lng)
                }
            }

    }
}