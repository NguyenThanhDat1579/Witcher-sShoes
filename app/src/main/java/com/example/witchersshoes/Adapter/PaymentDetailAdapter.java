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

public class PaymentDetailAdapter extends RecyclerView.Adapter<PaymentDetailAdapter.PaymentDetailViewHolder> {
    private Context context;
    private List<ProductModel> cartItems;

//    private CartUpdateListener cartUpdateListener;
    private FirebaseFirestore db;
    private String khachHangID; // Interface để cập nhật tổng giá

    public PaymentDetailAdapter(Context context, List<ProductModel> cartItems, CartUpdateListener cartUpdateListener) {
        this.context = context;
        this.cartItems = cartItems;
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
        ProductModel item = cartItems.get(position);

        // Hiển thị thông tin sản phẩm
        holder.titleTxt.setText(item.getTitle());
        holder.feeEachItem.setText(item.getPrice()+"00₫");
        holder.numberItemTxt.setText("x" + String.valueOf(item.getNumberInCart()));
        holder.totalEachItem.setText(String.format("%,.0f₫", (item.getPrice() * item.getNumberInCart()) * 1000));

        // Load ảnh sản phẩm bằng Glide
        Glide.with(context).load(item.getPicUrl().get(0)).into(holder.picCart);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
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

    public interface CartUpdateListener {
        void onCartUpdated(); // Interface để thông báo thay đổi giỏ hàng
    }

    private void updateCartItemQuantity(ProductModel item, int newQuantity, PaymentDetailViewHolder holder) {
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
//                                cartUpdateListener.onCartUpdated();
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
//                            cartUpdateListener.onCartUpdated();

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

