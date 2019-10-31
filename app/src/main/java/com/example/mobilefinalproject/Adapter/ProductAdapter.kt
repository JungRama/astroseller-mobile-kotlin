package com.example.mobilefinalproject.Adapter

import android.content.ClipData
import android.content.Context
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.mobilefinalproject.Fragments.SupplierFragment
import com.example.mobilefinalproject.Models.Product
import com.example.mobilefinalproject.R
import kotlinx.android.synthetic.main.card_product.view.*
import java.security.AccessControlContext

class RecyclerViewAdapter{

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
//        ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_product, parent, false))
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bindItem(items[position])
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
//
//        private val tv_product_name = view.findViewById<TextView>(R.id.tv_product_name)
//        private val tv_product_price = view.findViewById<TextView>(R.id.tv_product_price)
//        private val iv_product = view.findViewById<ImageView>(R.id.iv_product)
//
//        fun bindItem(items: ClipData.Item) {
//            tv_product_name.text = items.name
//            tv_product_price.text = items.name
//            items.image?.let { Picasso.get().load(it).fit().into(image) }
//        }
//    }
}