package com.example.mobilefinalproject

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_supplier_product_edit.*
import java.io.IOException


class SupplierProductEdit : AppCompatActivity() {

    lateinit var ivProduct: ImageView
    lateinit var etProductName: EditText
    lateinit var etProductPrice: EditText
    lateinit var etProductDescription: EditText
    lateinit var btnEditProduct: Button
    lateinit var btnDeleteProduct: Button
    lateinit var pbBar: ProgressBar

    var PICK_IMAGE_REQUEST = 71
    var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier_product_edit)

        //GET ID
        val product_id = intent.getStringExtra("id")

        // ACTION BAR
        val actionBar = supportActionBar
        actionBar!!.title = "Edit Product"
        actionBar.setDisplayHomeAsUpEnabled(true);

        // COMPONENT
        ivProduct = findViewById(R.id.ivProduct)
        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductDescription = findViewById(R.id.etProductDescription)
        btnEditProduct = findViewById(R.id.btnEditProduct)
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct)
        pbBar = findViewById(R.id.pbBar)

        // PRODUCT NAME
        var productName : String? = null

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

        // COLLECT USER DATA
        val docRef = db.collection("products").document(
            product_id)
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.DEFAULT
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                Glide
                    .with(applicationContext)
                    .load(document?.getString("product_image"))
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_product)
                    .into(ivProduct)

                // SET PRODUCT NAME
                productName = document?.getString("product_name")

                etProductName.setText(document?.getString("product_name"))
                etProductPrice.setText(document?.getString("product_price"))
                etProductDescription.setText(document?.getString("product_description"))


                Log.d("DATA", "Cached document data: ${document?.data}")
            } else {
                Log.d("DATA", "Cached get failed: ", task.exception)
            }
        }

        btnEditProduct.setOnClickListener {
            val productName = etProductName.getText().toString()
            val productPrice = etProductPrice.getText().toString()
            val productDescription = etProductDescription.getText().toString()

            if(productName.isEmpty()){
                etProductName.setError("Name is required")
                etProductName.requestFocus()
            }else if(productPrice.isEmpty()){
                etProductPrice.setError("Price is required")
                etProductPrice.requestFocus()
            }else{
                val progress = ProgressDialog.show(this, "",
                    "Please Wait ...", false);

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
                                    "product_name" to productName,
                                    "product_image" to it.toString(),
                                    "product_price" to productPrice,
                                    "product_description" to productDescription
                                )

                                db.collection("products")
                                    .document(product_id)
                                    .update(product as Map<String, Any>)
                                    .addOnSuccessListener { documentReference ->

                                        val toast = Toast.makeText(this, "Edit Product Success", Toast.LENGTH_SHORT)
                                        toast.show()

                                        progress.dismiss()

                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d("Main", "DocumentSnapshot added with ID: ${e}")
                                    }
                            }
                        }
                }else{
                    var product = hashMapOf(
                        "product_name" to productName,
                        "product_price" to productPrice,
                        "product_description" to productDescription
                    )

                    db.collection("products")
                        .document(product_id)
                        .update(product as Map<String, Any>)
                        .addOnSuccessListener { documentReference ->

                            val toast = Toast.makeText(this, "Edit Product Success", Toast.LENGTH_SHORT)
                            toast.show()

                            progress.dismiss()

                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.d("Main", "DocumentSnapshot added with ID: ${e}")
                        }
                }
            }
        }

        btnDeleteProduct.setOnClickListener {
            // Initialize a new instance of
            val builder = AlertDialog.Builder(this)

            // Set the alert dialog title
            builder.setTitle("Are you sure want to delete ?")

            // Display a message on alert dialog
            builder.setMessage("Are you sure want to delete ${productName} product")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("Yes"){dialog, which ->
                val progress = ProgressDialog.show(this, "",
                    "Please Wait ...", false);
                docRef.delete().addOnSuccessListener {
                    finish()
                    progress.dismiss()
                }
            }

            // Display a negative button on alert dialog
            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss();
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()
        }

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
