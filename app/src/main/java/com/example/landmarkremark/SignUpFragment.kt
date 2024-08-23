package com.example.landmarkremark

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.landmarkremark.common.Common
import com.example.landmarkremark.common.Common.Companion.isValidEmail
import com.example.landmarkremark.databinding.FragmentSignUpBinding
import com.example.landmarkremark.model.User

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity

        binding.edtEmail.doOnTextChanged { text, start, before, count ->
            if (!text.isValidEmail()) {
                binding.emailInputLayout.isErrorEnabled = true
                binding.emailInputLayout.error = "Invalid email"
            } else {
                binding.emailInputLayout.isErrorEnabled = false
                binding.emailInputLayout.error = null
            }
        }

        binding.edtPassword.doOnTextChanged { text, start, before, count ->
            if (text?.length in 6..10) {
                binding.passwordInputLayout.isErrorEnabled = false
                binding.passwordInputLayout.error = null
            } else {
                binding.passwordInputLayout.isErrorEnabled = true
                binding.passwordInputLayout.error = "Password must be less than 10 and more than 6 characters"
            }
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && !binding.emailInputLayout.isErrorEnabled && !binding.passwordInputLayout.isErrorEnabled) {
                mainActivity.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val userID = task.result.user!!.uid

                            val user = User(
                                id = userID,
                                name = binding.edtName.text.toString(),
                                email = email,
                            )

                            mainActivity.db.collection(Common.USERS)
                                .document(userID)
                                .set(user)
                                .addOnSuccessListener { documentReference ->
                                    showAlertDialog("Create account successfully", true)
                                }.addOnFailureListener { e ->
                                    showAlertDialog("Create account failed", false)
                                }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                this@SignUpFragment.context,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()

                            showAlertDialog(task.exception?.message, false)
                        }
                    }
            }
        }

        binding.btnSignIn.setOnClickListener {
            showLoginFragment()
        }
    }

    private fun showAlertDialog(message: String? , isSuccess : Boolean) {
        val alertDialog = AlertDialog.Builder(this@SignUpFragment.context)
        alertDialog.setCancelable(false)
        alertDialog.setMessage(message)
        alertDialog.setNeutralButton("Ok") {dialog, which ->
            dialog.dismiss()
            if (isSuccess) {
                showLoginFragment()
            }
        }
        alertDialog.show()
    }

    private fun showLoginFragment() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, LoginFragment.newInstance())
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}