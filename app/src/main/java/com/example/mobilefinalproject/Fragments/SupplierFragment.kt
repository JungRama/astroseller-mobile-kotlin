package com.example.mobilefinalproject.Fragments

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilefinalproject.Adapter.AdapterProduct
import com.example.mobilefinalproject.Models.ProductModel
import com.example.mobilefinalproject.R
import com.example.mobilefinalproject.SupplierProductAdd
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

class SupplierFragment : Fragment(){

    companion object {
        fun newInstance(): SupplierFragment{
            return SupplierFragment()
        }
    }

    lateinit var form_supplier : LinearLayout
    lateinit var list_product : androidx.recyclerview.widget.RecyclerView
    lateinit var etShopName : EditText
    lateinit var etShopDescription  : EditText
    lateinit var pbBar      : ProgressBar
    lateinit var btnRegisterSupplier    : Button
    lateinit var fab_add_product : FloatingActionButton

    val db = FirebaseFirestore.getInstance()

    var SUPPLIER_STATUS : Boolean = false

    lateinit var adapter : AdapterProduct

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view =  inflater.inflate(R.layout.content_supplier, container, false)

        etShopName = view.findViewById<EditText>(R.id.etShopName)
        etShopDescription  = view.findViewById<EditText>(R.id.etShopDescription)
        pbBar  = view.findViewById<ProgressBar>(R.id.pbBar)
        btnRegisterSupplier  = view.findViewById<Button>(R.id.btnRegisterSupplier)
        form_supplier  = view.findViewById<LinearLayout>(R.id.form_supplier)
        fab_add_product = view.findViewById<FloatingActionButton>(R.id.fab_add_product)
        list_product  = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list_product)

        pbBar.setVisibility(View.VISIBLE)

        // LOAD PRODUCT
        loadProduct()

        // ITEM ON CLICK
        adapter.setOnItemClickListener { documentSnapshot, position ->
            Toast.makeText(this.context, "id ${documentSnapshot.id}", Toast.LENGTH_LONG).show();
        }

        // COLLECT USER DATA
        val docRef = db.collection("users").document(
            FirebaseAuth.getInstance().currentUser!!.uid.toString())
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.DEFAULT
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                if (document != null) {
                    SUPPLIER_STATUS = document.getBoolean("supplier_status")!!
                }

                if(SUPPLIER_STATUS==false){
                    form_supplier.setVisibility(View.VISIBLE)
                }else{
                    list_product.setVisibility(View.VISIBLE)
                    fab_add_product.show()



                }

                Log.d("DATA", "Cached document data: ${document?.data}")

                pbBar.setVisibility(View.GONE)
            } else {
                Log.d("DATA", "Cached get failed: ", task.exception)
            }
        }

        // REGISTER SUPPLIER
        btnRegisterSupplier.setOnClickListener {
            val shopName = etShopName.getText().toString()
            val shopDescription = etShopDescription.getText().toString()

            if (shopName.isEmpty()){
                etShopName.setError("Shop Name is required");
                etShopName.requestFocus();
            }else if(shopDescription.isEmpty()){
                etShopDescription.setError("Shop Description is required");
                etShopDescription.requestFocus();
            }else{

                pbBar.setVisibility(View.VISIBLE)

                var user = hashMapOf(
                    "shop_name" to shopName,
                    "shop_description" to shopDescription,
                    "supplier_status" to true
                )

                db.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
                    .update(user as Map<String, Any>)
                    .addOnSuccessListener { documentReference ->

                        val toast = Toast.makeText(context!!, "Edit Profile Success", Toast.LENGTH_SHORT)
                        toast.show()

                        pbBar.setVisibility(View.GONE)

                        getFragmentManager()!!.beginTransaction().replace(R.id.fragment_content, SupplierFragment.newInstance()).commit();
                    }
                    .addOnFailureListener { e ->
                        Log.d("Main", "DocumentSnapshot added with ID: ${e}")
                    }
            }
        }

        // INTENT TO ADDED PRODUCT
        fab_add_product.setOnClickListener {
            val intent = Intent(context, SupplierProductAdd::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadProduct() {
        val query : Query = db.collection("products")

        val options =
            FirestoreRecyclerOptions.Builder<ProductModel>().
                setQuery(query, ProductModel::class.java)
            .build()

        adapter = AdapterProduct(options)

        list_product.setHasFixedSize(true)
        list_product.layoutManager = LinearLayoutManager(activity)
        list_product.setAdapter(adapter)

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}