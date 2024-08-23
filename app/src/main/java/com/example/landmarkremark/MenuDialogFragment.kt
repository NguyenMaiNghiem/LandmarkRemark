package com.example.landmarkremark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.landmarkremark.databinding.FragmentMenuBinding
import com.example.landmarkremark.mynotes.MyNotesDialogFragment
import com.example.landmarkremark.viewmodel.MyViewModel

class MenuDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentMenuBinding
    private val userMyViewModel : MyViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapsActivity = requireActivity() as MapsActivity

        binding.userNameTxt.text = userMyViewModel.user.value?.name
        binding.userEmailTxt.text = userMyViewModel.user.value?.email

        binding.signOutLayout.setOnClickListener {
            mapsActivity.auth.signOut()
            mapsActivity.transitToMainActivity()
        }

        binding.listNotesLayout.setOnClickListener {
            showListNotesFragment()
        }
    }

    private fun showListNotesFragment() {
        val menuDialogFragment = MyNotesDialogFragment.newInstance()
        menuDialogFragment.show(requireActivity().supportFragmentManager,"MenuFragment")
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuDialogFragment()
    }
}