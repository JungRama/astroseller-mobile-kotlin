package com.example.mobilefinalproject.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipDescription
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mobilefinalproject.R
import de.hdodenhof.circleimageview.CircleImageView
import android.content.Intent
import android.provider.MediaStore
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.*
import com.example.mobilefinalproject.LoginActivity
import com.example.mobilefinalproject.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.HashMap


class EditProfileFragment : Fragment() {
    companion object {
        fun newInstance(): EditProfileFragment {
            return EditProfileFragment()
        }
    }

    lateinit var etName     : EditText
    lateinit var etPhone    : EditText
    lateinit var etAddress  : EditText
    lateinit var etShopName : EditText
    lateinit var etShopDescription  : EditText
    lateinit var pbBar      : ProgressBar
    lateinit var btnSave    : Button

    lateinit var form_supplier : LinearLayout

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    var SUPPLIER_STATUS : Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.content_edit_profile, container, false)

        // FIREBASE
        val db = FirebaseFirestore.getInstance()

        // SHARED PREFERENCE
        val sharedPreference: SharedPreference = SharedPreference(context!!)

        val etName  = view.findViewById<EditText>(R.id.etName)
        val etPhone  = view.findViewById<EditText>(R.id.etPhone)
        val etAddress  = view.findViewById<EditText>(R.id.etAddress)
        val etShopName  = view.findViewById<EditText>(R.id.etShopName)
        val etShopDescription  = view.findViewById<EditText>(R.id.etShopDescription)
        val pbBar  = view.findViewById<ProgressBar>(R.id.pbBar)
        val btnSave  = view.findViewById<Button>(R.id.btnSave)
        val form_supplier  = view.findViewById<LinearLayout>(R.id.form_supplier)

        // COLLECT USER DATA
        val docRef = db.collection("users").document(
            FirebaseAuth.getInstance().currentUser!!.uid.toString())
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.DEFAULT
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                etName.setText(document?.getString("name"))
                etPhone.setText(document?.getString("phone"))
                etAddress.setText(document?.getString("address"))
                etShopName.setText(document?.getString("shop_name"))
                etShopDescription.setText(document?.getString("shop_description"))

                SUPPLIER_STATUS = document?.getBoolean("supplier_status")!!

                if(SUPPLIER_STATUS!=false){
                    form_supplier.setVisibility(View.VISIBLE)
                }

                Log.d("DATA", "Cached document data: ${document?.data}")
            } else {
                Log.d("DATA", "Cached get failed: ", task.exception)
            }
        }

        // SAVE
        btnSave.setOnClickListener {
            val name    = etName.getText().toString()
            val phone   = etPhone.getText().toString()
            val address = etAddress.getText().toString()
            val shopName = etShopName.getText().toString()
            val shopDescription = etShopDescription.getText().toString()

            if (name.isEmpty()){
                etName.setError("Email is required");
                etName.requestFocus();
            }else{
                var user : HashMap<String, String>

                if(SUPPLIER_STATUS!=false){
                    user = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "address" to address,
                        "shop_name" to shopName,
                        "shop_description" to shopDescription
                    )

                    if (shopName.isEmpty()){
                        etShopName.setError("Shop Name is required");
                        etShopName.requestFocus();
                    }else if(shopDescription.isEmpty()){
                        etShopDescription.setError("Shop Description is required");
                        etShopDescription.requestFocus();
                    }

                }else{
                    user = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "address" to address
                    )
                }

                val progress = ProgressDialog.show(activity, "",
                    "Please Wait ...", false);

                db.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
                    .update(user as Map<String, Any>)
                    .addOnSuccessListener { documentReference ->

                        val toast = Toast.makeText(context!!, "Edit Profile Success", Toast.LENGTH_SHORT)
                        toast.show()

                        progress.dismiss()

                    }
                    .addOnFailureListener { e ->
                        Log.d("Main", "DocumentSnapshot added with ID: ${e}")
                        progress.dismiss()
                    }
            }
        }

        return view
    }
}