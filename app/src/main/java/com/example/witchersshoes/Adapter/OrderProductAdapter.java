package com.example.witchersshoes.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.witchersshoes.Model.Product;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.PaymentDetailViewHolder> {
    private Context context;
    private List<Product> listProduct;

//    private CartUpdateListener cartUpdateListener;
    private FirebaseFirestore db;
    private String khachHangID; // Interface để cập nhật tổng giá

    public OrderProductAdapter(Context context, List<Product> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
//        this.cartUpdateListener = cartUpdateListener;
        this.db = FirebaseFirestore.getInstance();

        // Lấy khachHangID từ SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("THONGTIN", Context.MODE_PRIVATE);
        this.khachHangID = preferences.getString("khachHangID", null);
    }


    @NonNull
    @Override
    public PaymentDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_payment_detail, parent, false);
        return new PaymentDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentDetailViewHolder holder, int position) {
        Product item = listProduct.get(position);

        // Hiển thị thông tin sản phẩm
        holder.titleTxt.setText(item.getProductName());
        holder.feeEachItem.setText(item.getPrice()+"00₫");
        holder.numberItemTxt.setText("x" + String.valueOf(item.getQuantity()));
        holder.totalEachItem.setText(String.format("%,.0f₫", Double.parseDouble(item.getPrice()) * item.getQuantity() * 1000));

        // Load ảnh sản phẩm bằng Glide
        Glide.with(context).load(item.getImage()).into(holder.picCart);
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public static class PaymentDetailViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, feeEachItem, totalEachItem, numberItemTxt;
        ShapeableImageView picCart;

        public PaymentDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
            picCart = itemView.findViewById(R.id.picCart);

        }
    }
}

