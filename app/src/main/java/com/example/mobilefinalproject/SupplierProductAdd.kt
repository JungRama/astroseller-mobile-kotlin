package com.example.mobilefinalproject

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.example.mobilefinalproject.Fragments.SupplierFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.*
import android.webkit.MimeTypeMap
import android.content.ContentResolver


class SupplierProductAdd : AppCompatActivity() {

    lateinit var ivProduct: ImageView
    lateinit var etProductName: EditText
    lateinit var etProductPrice: EditText
    lateinit var etProductDescription: EditText
    lateinit var btnAddProduct: Button
    lateinit var pbBar: ProgressBar

    var PICK_IMAGE_REQUEST = 71
    var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_product_add)

        // ACTION BAR
        val actionBar = supportActionBar
        actionBar!!.title = "Add Product"
        actionBar.setDisplayHomeAsUpEnabled(true);

        // COMPONENT
        ivProduct = findViewById(R.id.ivProduct)
        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductDescription = findViewById(R.id.etProductDescription)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        pbBar = findViewById(R.id.pbBar)

        // FIREBASE
        val db = FirebaseFirestore.getInstance()
        val storageReference = FirebaseStorage.getInstance().getReference()

        // SELECT IMAGE
        ivProduct.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }


        // INSERT IMAGE TO STORAGE
        btnAddProduct.setOnClickListener {

            pbBar.setVisibility(View.VISIBLE)

            val productName = etProductName.getText().toString()
            val productPrice = etProductPrice.getText().toString()
            val productDescription = etProductDescription.getText().toString()


            if (filePath != null) {

                // IMAGE LOCATION
                var productStorage =
                    storageReference.child(
                        "product/" +
                                System.currentTimeMillis() + "." + GetFileExtension(filePath!!)
                    )

                var imageToUpload = productStorage.putFile(filePath!!)

                // UPLOAD TO STORAGE
                imageToUpload
                    .addOnSuccessListener { task ->

                        // GET DOWNLOADED IMAGE
                        productStorage.downloadUrl.addOnSuccessListener {

                            Log.d("downloadURL", it.toString())
                                    pbBar.setVisibility(View.GONE)

                            var product = hashMapOf(
                                "user_id" to FirebaseAuth.getInstance().currentUser!!.uid.toString(),
                                "product_name" to productName,
                                "product_image" to it.toString(),
                                "product_price" to productPrice,
                                "product_description" to productDescription
                            )

                            db.collection("products")
                                .document()
                                .set(product as Map<String, Any>)
                                .addOnSuccessListener { documentReference ->

                                    val toast = Toast.makeText(this, "Add Product Success", Toast.LENGTH_SHORT)
                                    toast.show()

                                    pbBar.setVisibility(View.GONE)

                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.d("Main", "DocumentSnapshot added with ID: ${e}")
                                }


                        }
                    }
            }


//
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null && data!!.data == null) {
                return
            }
            filePath = data.data
            try {
//                val imageFile : Image
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                ivProduct.setImageBitmap(bitmap)
            } catch (e: IOException) {
                Log.d("error", e.toString())
            }
        }
    }

    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

}
