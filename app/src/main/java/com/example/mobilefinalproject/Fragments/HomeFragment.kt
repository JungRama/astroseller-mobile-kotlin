package com.example.mobilefinalproject.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilefinalproject.Adapter.AdapterProduct
import com.example.mobilefinalproject.Models.ProductModel
import com.example.mobilefinalproject.ProductDetail
import com.example.mobilefinalproject.R
import com.example.mobilefinalproject.SupplierProductEdit
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    lateinit var list_product : androidx.recyclerview.widget.RecyclerView
    lateinit var iv_banner : ImageView
    lateinit var adapter : AdapterProduct

    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.content_home, container, false)

        list_product  = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list_product_home)
//        iv_banner = view.findViewById(R.id.iv_banner)

        //
        list_product.setVisibility(View.VISIBLE)
        loadProduct()
        adapter.startListening()
        Log.d("CHECKINGLF", "ONCREATE")



        return view
    }

    private fun loadProduct(){
        val query : Query = db.collection("products")

        val options =
            FirestoreRecyclerOptions.Builder<ProductModel>().
                setQuery(query, ProductModel::class.java)
                .build()

        adapter = AdapterProduct(options)

        // ITEM ON CLICK
        adapter.setOnItemClickListener { documentSnapshot, position ->
            val intent = Intent(context, ProductDetail::class.java)
            intent.putExtra("id", documentSnapshot.id)
            startActivity(intent)
        }


        list_product.setHasFixedSize(true)
        list_product.layoutManager = LinearLayoutManager(context)
        list_product.setAdapter(adapter)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        Log.d("CHECKINGLF", "ONSTART")
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
        Log.d("CHECKINGLF", "ONRESUME")

    }

}