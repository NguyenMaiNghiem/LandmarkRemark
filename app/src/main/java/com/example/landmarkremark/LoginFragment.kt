package com.example.landmarkremark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.landmarkremark.common.Common
import com.example.landmarkremark.databinding.FragmentLoginBinding
import com.example.landmarkremark.model.User
import com.example.landmarkremark.viewmodel.MyViewModel
import com.google.firebase.firestore.toObject

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val userMyViewModel : MyViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity

        binding.btnSignIn.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                mainActivity.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information

                            mainActivity.db.collection(Common.USERS)
                                .document(task.result.user!!.uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val user = document.toObject<User>()
                                        userMyViewModel.user.value = user!!
                                        mainActivity.transitToMapsActivity()
                                    } else {
                                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener { exception ->
                                    Toast.makeText(requireContext(), "Error: $exception", Toast.LENGTH_SHORT).show()
                                }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT,).show()
                        }
                    }
            }
        }

        binding.btnSignUp.setOnClickListener {
            val signUpFragment = SignUpFragment.newInstance()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, signUpFragment)
                .commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}