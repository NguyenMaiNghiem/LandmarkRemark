package com.example.landmarkremark

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.example.landmarkremark.allnote.NoteLocationDialogFragment
import com.example.landmarkremark.common.Common
import com.example.landmarkremark.databinding.ActivityMapsBinding
import com.example.landmarkremark.model.Notes
import com.example.landmarkremark.mynotes.MyNotesDialogFragment
import com.example.landmarkremark.search.SearchDialogFragment
import com.example.landmarkremark.viewmodel.MyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MapsActivity : AppCompatActivity(),
    GoogleMap.OnInfoWindowClickListener,
    OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentLocation: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val myViewModel: MyViewModel by viewModels()
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        Common.currentUser?.let {
            myViewModel.user.value = it
        }

        if (onRequestPermissions()) {
            getLastLocation()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val searchView = binding.searchView
        searchView.requestFocusFromTouch()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchNotes(query)
                showSearchDialogFragment()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.avatarBtn.setOnClickListener {
            openMenuFragment()
        }

        binding.myLocationBtn.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
        }
    }


    private fun searchNotes(query: String?) {
        val resultSearchNotes = mutableListOf<Notes>()
        if (!query.isNullOrEmpty() && myViewModel.mutableLiveDataAllNotes.value != null) {
            for (note in myViewModel.mutableLiveDataAllNotes.value!!) {
                if (note.name!!.lowercase().contains(query.lowercase()) ||
                    note.title!!.lowercase().contains(query.lowercase()) ||
                    note.description!!.lowercase().contains(query.lowercase())){
                    resultSearchNotes.add(note)
                }
            }
            myViewModel.mutableLiveDataSearchNotes.value = resultSearchNotes
        }
    }

    private fun showSearchDialogFragment() {
        var searchDialogFragment = supportFragmentManager.findFragmentByTag("SearchDialogFragment") as DialogFragment?

        if (searchDialogFragment == null) {
            searchDialogFragment = SearchDialogFragment.newInstance(binding.searchView.measuredHeight)
        }
        if (!searchDialogFragment.isVisible){
            searchDialogFragment.show(supportFragmentManager, "SearchDialogFragment")
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true        // Điều khiển thu phóng
        mMap.uiSettings.isCompassEnabled = true             // La bàn
        mMap.uiSettings.isScrollGesturesEnabled = true      // Cử chỉ cuộn
        mMap.uiSettings.isZoomGesturesEnabled = true        // Cử chỉ thu phóng
        mMap.uiSettings.isTiltGesturesEnabled = true        // Cử chỉ nghiêng
        mMap.uiSettings.isRotateGesturesEnabled = true      // Cử chỉ xoay

        mMap.setOnInfoWindowClickListener(this)
    }

    private fun onRequestPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)

                    setDataToMyLocation()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "getLastLocation failed $exception", Toast.LENGTH_LONG).show()
            }
    }

    override fun onInfoWindowClick(marker: Marker) {
        showNoteLocationDialogFragment(marker.position.latitude, marker.position.longitude)
    }

    fun showAddNoteDialogFragment(lat: Double, lng: Double) {
        val addDialog = Dialog(this)
        addDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.fragment_add_note_dialog)
        }

        val window = addDialog.window
        window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.8).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)

            val windowAttribution = window.attributes
            windowAttribution.gravity = android.view.Gravity.CENTER
            attributes = windowAttribution

            val titleInputLayout = addDialog.findViewById<TextInputLayout>(R.id.title_input_layout)
            val titleEdt = addDialog.findViewById<EditText>(R.id.titleEdt)
            val descriptionInputLayout =
                addDialog.findViewById<TextInputLayout>(R.id.description_input_layout)
            val descriptionEdt = addDialog.findViewById<EditText>(R.id.descriptionEditText)
            val cancelBtn = addDialog.findViewById<Button>(R.id.cancelBtn)
            val addBtn = addDialog.findViewById<Button>(R.id.addBtn)

            titleEdt.doOnTextChanged { text, start, before, count ->
                if (text.isNullOrEmpty()) {
                    titleInputLayout.error = "Please enter title"
                    titleInputLayout.isErrorEnabled = true
                } else {
                    titleInputLayout.error = null
                    titleInputLayout.isErrorEnabled = false
                }

            }

            descriptionEdt.doOnTextChanged { text, start, before, count ->
                if (text.isNullOrEmpty()) {
                    descriptionInputLayout.error = "Please enter description"
                    descriptionInputLayout.isErrorEnabled = true
                } else {
                    descriptionInputLayout.error = null
                    descriptionInputLayout.isErrorEnabled = false
                }

            }

            cancelBtn.setOnClickListener {
                addDialog.dismiss()
            }
            addBtn.setOnClickListener {
                val markerTitle = titleEdt.text.toString()
                val markerSnippet = descriptionEdt.text.toString()

                if (markerTitle.isNotEmpty() && markerSnippet.isNotEmpty()) {
                    // If add success -> save Note to Firebase -> dismiss dialog
                    saveNote(titleEdt.text.toString(), descriptionEdt.text.toString(), lat, lng)
                    addDialog.dismiss()
                }
            }
        }

        addDialog.setCancelable(true)
        addDialog.show()
    }

    private fun saveNote(title: String, description: String, lat: Double, lng: Double) {
        val notes = Notes(
            id = myViewModel.user.value?.id,
            name = myViewModel.user.value?.name,
            email = myViewModel.user.value?.email,
            title = title,
            description = description,
            currentLocation = listOf(lat, lng)
        )

        db.collection(Common.NOTES)
            .add(notes)
            .addOnSuccessListener {
                transitToLocation(title, description, LatLng(lat, lng))
                Toast.makeText(this, "saveNote success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "saveNote failed", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setDataToMyLocation() = CoroutineScope(Dispatchers.Main).launch {
        var title = ""
        var snippet = ""
        try {
            val documents = getDocumentsFromFireStore()
            for (document in documents) {
                title = document.data?.get("title")?.toString() ?: ""
                snippet = document.data?.get("description")?.toString() ?: ""
            }
        } catch (exception: Exception) {
            title = "My Location"
            snippet = "Lat : ${currentLocation.latitude} - Long : ${currentLocation.longitude}"
        }

        if (title.isEmpty() || snippet.isEmpty()) {
            title = "My Location"
            snippet = "Lat : ${currentLocation.latitude} - Long : ${currentLocation.longitude}"
        }

        mMap.addMarker(
            MarkerOptions().position(currentLocation).title(title)
                .snippet(snippet)

        )?.showInfoWindow()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
    }

    private suspend fun getDocumentsFromFireStore(): List<DocumentSnapshot> =
        suspendCancellableCoroutine { continuation ->
            db.collection(Common.NOTES)
                .whereEqualTo(
                    "currentLocation",
                    listOf(currentLocation.latitude, currentLocation.longitude)
                )
                .whereEqualTo("id", myViewModel.user.value?.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    continuation.resume(querySnapshot.documents)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    private fun openMenuFragment() {
        val menuDialogFragment = MenuDialogFragment.newInstance()
        menuDialogFragment.show(supportFragmentManager,"MenuFragment")
    }

    private fun showNoteLocationDialogFragment(lat: Double, lng: Double) {
        val noteLocationFragment = NoteLocationDialogFragment.newInstance(lat, lng)
        noteLocationFragment.show(supportFragmentManager,"NoteLocationDialogFragment")
    }

    fun transitToMainActivity() {
        Common.currentUser = myViewModel.user.value
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun transitToLocation(title: String, snippet: String, currentLocation: LatLng) {
        mMap.addMarker(
            MarkerOptions().position(currentLocation).title(title)
                .snippet(snippet)

        )?.showInfoWindow()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))

        val fragmentManager = supportFragmentManager
        val fragments = fragmentManager.fragments

        for (fragment in fragments) {
            when(fragment) {
                is NoteLocationDialogFragment -> fragment.dismiss()
                is MyNotesDialogFragment -> fragment.dismiss()
                is MenuDialogFragment -> fragment.dismiss()
                is SearchDialogFragment -> fragment.dismiss()
            }
        }
    }
}
