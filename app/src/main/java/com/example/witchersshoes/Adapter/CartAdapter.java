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
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<ProductModel> cartItems;
    private CartUpdateListener cartUpdateListener;
    private FirebaseFirestore db;
    private String khachHangID; // Interface để cập nhật tổng giá

    public CartAdapter(Context context, List<ProductModel> cartItems, CartUpdateListener cartUpdateListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartUpdateListener = cartUpdateListener;
        this.db = FirebaseFirestore.getInstance();

        // Lấy khachHangID từ SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("THONGTIN", Context.MODE_PRIVATE);
        this.khachHangID = preferences.getString("khachHangID", null);
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel item = cartItems.get(position);

        // Hiển thị thông tin sản phẩm
        holder.titleTxt.setText(item.getTitle());
        holder.feeEachItem.setText(item.getPrice()+"00₫");
        holder.numberItemTxt.setText(String.valueOf(item.getNumberInCart()));
        holder.totalEachItem.setText((item.getPrice() * item.getNumberInCart())+"00₫");

        // Load ảnh sản phẩm bằng Glide
        Glide.with(context).load(item.getPicUrl().get(0)).into(holder.picCart);

        // Xử lý tăng số lượng
        holder.plusCartBtn.setOnClickListener(v -> {
            int newQuantity = item.getNumberInCart() + 1;
            updateCartItemQuantity(item, newQuantity, holder);
        });

        // Xử lý giảm số lượng
        holder.minusCartBtn.setOnClickListener(v -> {
            if (item.getNumberInCart() > 0) {
                int newQuantity = item.getNumberInCart() - 1;
                updateCartItemQuantity(item, newQuantity, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, feeEachItem, totalEachItem, numberItemTxt;
        ShapeableImageView picCart;
        TextView plusCartBtn, minusCartBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
            picCart = itemView.findViewById(R.id.picCart);
            plusCartBtn = itemView.findViewById(R.id.plusCartBtn);
            minusCartBtn = itemView.findViewById(R.id.minusCartBtn);
        }
    }

    public interface CartUpdateListener {
        void onCartUpdated(); // Interface để thông báo thay đổi giỏ hàng
    }

    private void updateCartItemQuantity(ProductModel item, int newQuantity, CartViewHolder holder) {
        if (khachHangID != null) {
            // Tạo reference đến document của sản phẩm trong giỏ hàng
            DocumentReference itemRef = db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Cart")
                    .document(item.getID());

            if (newQuantity <= 0) {
                // Xóa sản phẩm khỏi giỏ hàng
                itemRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            // Xóa item khỏi list và cập nhật UI
                            int position = cartItems.indexOf(item);
                            if (position != -1) {
                                cartItems.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, cartItems.size());
                                notifyDataSetChanged();
                                cartUpdateListener.onCartUpdated();
                            } else {
                                Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Lỗi khi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Cập nhật số lượng trên Firestore
                itemRef.update("soLuong", newQuantity)
                        .addOnSuccessListener(aVoid -> {
                            // Cập nhật UI sau khi update thành công
                            item.setNumberInCart(newQuantity);
                            holder.numberItemTxt.setText(String.valueOf(newQuantity));
                            holder.totalEachItem.setText((item.getPrice() * newQuantity)+"00₫");
                            cartUpdateListener.onCartUpdated();

                            // Thông báo thành công (tùy chọn)
                            Toast.makeText(context, "Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý khi update thất bại
                            Toast.makeText(context, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(context, "Vui lòng đăng nhập để cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }
}

