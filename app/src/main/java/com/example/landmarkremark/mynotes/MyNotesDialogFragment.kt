package com.example.landmarkremark.mynotes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landmarkremark.MapsActivity
import com.example.landmarkremark.databinding.FragmentMyNotesBinding
import com.example.landmarkremark.viewmodel.MyViewModel
import com.google.android.gms.maps.model.LatLng

class MyNotesDialogFragment : DialogFragment(), OnMyNoteListener {

    private lateinit var binding: FragmentMyNotesBinding
    private val myViewModel : MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyNotesBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        myViewModel.getMyNote()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.notesRecyclerView

        myViewModel.mutableLiveDataMyNotes.observe(viewLifecycleOwner) {
            recyclerView.apply {
                adapter = MyNotesAdapter(myViewModel.mutableLiveDataMyNotes.value, this@MyNotesDialogFragment)
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))
            }
        }

        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.95).toInt()
        dialog!!.window!!.setLayout(width, height)
    }

    override fun onNoteItemClick(position: Int) {
        myViewModel.mutableLiveDataMyNotes.value?.let {
            val title = it[position].title!!
            val snippet = it[position].description!!
            val lat = it[position].currentLocation!![0]
            val lng = it[position].currentLocation!![1]
            val location = LatLng(lat, lng)
            val mapsActivity = (requireActivity() as MapsActivity)
            mapsActivity.transitToLocation(title, snippet, location)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyNotesDialogFragment()
    }
}