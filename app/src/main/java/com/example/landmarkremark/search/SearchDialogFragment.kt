package com.example.landmarkremark.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landmarkremark.MapsActivity
import com.example.landmarkremark.databinding.FragmentSearchDialogBinding
import com.example.landmarkremark.viewmodel.MyViewModel
import com.google.android.gms.maps.model.LatLng

private const val SEARCH_VIEW_HEIGHT = "searchViewHeight"

class SearchDialogFragment : DialogFragment(), OnSearchNoteListener {
    private var searchViewHeight: Int? = null
    private lateinit var binding: FragmentSearchDialogBinding
    private val myViewModel: MyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchViewHeight = it.getInt(SEARCH_VIEW_HEIGHT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchDialogBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.searchNotesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        myViewModel.mutableLiveDataSearchNotes.observe(viewLifecycleOwner) {
            recyclerView.adapter = SearchNotesAdapter(it, this)
            recyclerView.adapter?.notifyDataSetChanged()
        }

        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.y = 100 // Khoảng cách từ top (đơn vị là pixel)

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels - (searchViewHeight ?: 0)

        dialog?.apply {
            window?.attributes = params as WindowManager.LayoutParams
            window?.setLayout(width, height)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(searchViewHeight: Int) = SearchDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(SEARCH_VIEW_HEIGHT, searchViewHeight)
            }
        }
    }

    override fun onSearchNoteItemClick(position: Int) {
        myViewModel.mutableLiveDataSearchNotes.value?.let {
            val title = it[position].title!!
            val snippet = it[position].description!!
            val lat = it[position].currentLocation!![0]
            val lng = it[position].currentLocation!![1]
            val location = LatLng(lat, lng)
            val mapsActivity = (requireActivity() as MapsActivity)
            mapsActivity.transitToLocation(title, snippet, location)
        }

    }
}