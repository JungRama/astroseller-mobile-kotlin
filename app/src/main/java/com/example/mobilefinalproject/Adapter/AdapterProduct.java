package com.example.mobilefinalproject.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobilefinalproject.Models.ProductModel;
import com.example.mobilefinalproject.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class AdapterProduct extends FirestoreRecyclerAdapter<ProductModel, AdapterProduct.ProductHolder> {

    private OnItemClickListener listener;

    public AdapterProduct(@NonNull FirestoreRecyclerOptions<ProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull ProductModel model) {
        holder.tv_product_name.setText(model.getProduct_name());
        holder.tv_product_price.setText("Rp. " + model.getProduct_price());

        if(model.getProduct_image()!=null){
//            Picasso.with(holder.iv_product.getContext())
//                    .load(model.getProduct_image())
////                    .placeholder(R.drawable.placeholder_product)
//                    .into(holder.iv_product);

            Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(holder.iv_product);
        }
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product,
                parent, false);
        return new ProductHolder(v);
    }

    class ProductHolder extends RecyclerView.ViewHolder{
        TextView tv_product_name;
        TextView tv_product_price;
        ImageView iv_product;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_price = itemView.findViewById(R.id.tv_product_price);
            iv_product = itemView.findViewById(R.id.iv_product);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position  = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener !=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
