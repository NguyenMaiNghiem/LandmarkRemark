package com.example.landmarkremark.common

import android.util.Patterns
import com.example.landmarkremark.model.Notes
import com.example.landmarkremark.model.User

class Common {
    companion object {
        var currentUser: User? = null
        var allNotes: Notes? = null
        const val USERS = "Users"
        const val NOTES = "Notes"

        fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}

