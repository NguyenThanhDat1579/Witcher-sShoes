package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.CartAdapter;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.R;
import com.example.witchersshoes.databinding.ActivityCartBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartView;
    private ImageView backBtn;
    private Button btnPayment;
    private TextView totalFeeTxt, taxTxt, deliveryTxt, totalTxt;
    private CartAdapter cartAdapter;
    private List<ProductModel> cartItems = new ArrayList<>();
    private double totalFee = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ view
        cartView = findViewById(R.id.cartView);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        taxTxt = findViewById(R.id.taxTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        backBtn = findViewById(R.id.backBtn);
        btnPayment = findViewById(R.id.btnPayment);

        // Cài đặt RecyclerView
        cartView.setLayoutManager(new LinearLayoutManager(this));

        // Tải dữ liệu giỏ hàng từ Firestore
        loadCartItems();

        backBtn.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, MainActivity.class)));
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, PaymentDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadCartItems() {
        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        String khachHangID = preferences.getString("khachHangID", null);

        if (khachHangID != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Cart")
                    .addSnapshotListener((value, error) -> { // Sử dụng addSnapshotListener để cập nhật realtime
                        if (error != null) {
                            Toast.makeText(this, "Lỗi khi tải giỏ hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            cartItems.clear();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                ProductModel item = new ProductModel();
                                // Lưu ID của document làm ID sản phẩm
                                item.setID(document.getId());
                                item.setTitle(document.getString("tenSanPham"));
                                item.setNumberInCart(document.getLong("soLuong") != null ?
                                        document.getLong("soLuong").intValue() : 1);
                                item.setPrice(document.getDouble("giaTien") != null ?
                                        document.getDouble("giaTien") : 0.0);

                                String imageUrl = document.getString("hinhAnh");
                                item.setPicUrl(new ArrayList<>(Collections.singletonList(
                                        imageUrl != null ? imageUrl : "default_image_url")));

                                cartItems.add(item);
                            }

                            // Cập nhật UI
                            if (cartAdapter == null) {
                                cartAdapter = new CartAdapter(CartActivity.this, cartItems,
                                        CartActivity.this::calculateCartTotal);
                                cartView.setAdapter(cartAdapter);
                            } else {
                                cartAdapter.notifyDataSetChanged();
                            }

                            calculateCartTotal();

                            // Hiển thị thông báo nếu giỏ hàng trống
                            if (cartItems.isEmpty()) {
                                Toast.makeText(CartActivity.this,
                                        "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CartActivity.this, MainActivity.class));
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
            // Có thể chuyển hướng đến màn hình đăng nhập
            // startActivity(new Intent(CartActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void calculateCartTotal() {
        totalFee = 0;
        for (ProductModel item : cartItems) {
            totalFee += item.getPrice() * item.getNumberInCart();
        }

        double tax = totalFee * 0.1; // Thuế 10%
        double deliveryFee = 20; // Phí vận chuyển cố định

        totalFeeTxt.setText(totalFee+"00₫");
        taxTxt.setText(tax+"00₫");
        deliveryTxt.setText(deliveryFee+"00₫");
        totalTxt.setText((totalFee + tax + deliveryFee)+"00₫");
    }
}
