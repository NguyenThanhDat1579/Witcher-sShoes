package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.CartAdapter;
import com.example.witchersshoes.Adapter.PaymentDetailAdapter;
import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaymentDetailActivity extends AppCompatActivity {

    private RecyclerView paymentDetailView;
    private ImageView backBtn;
    private Button btnOrder;
    private TextView txtName, txtPhone, txtLocation, totalFeeTxt, taxTxt, deliveryTxt, totalTxt;
    private PaymentDetailAdapter paymentDetailAdapter;
    private List<ProductModel> cartItems = new ArrayList<>();
    private double totalFee = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        paymentDetailView = findViewById(R.id.paymentDetailView);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtLocation = findViewById(R.id.txtLocation);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        taxTxt = findViewById(R.id.taxTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        backBtn = findViewById(R.id.backBtn);
        btnOrder = findViewById(R.id.btnOrder);

        paymentDetailView.setLayoutManager(new LinearLayoutManager(this));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        loadCartItems();

    }

// Lấy thông tin nguời dùng

    private void loadDataUser(String khachHangID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("KhachHang")
                .document(khachHangID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Customer customer = new Customer();
                        customer.setId(documentSnapshot.getId());
                        customer.setUsername(documentSnapshot.getString("tenKhachHang"));
                        customer.setPhone(documentSnapshot.getString("soDienThoai"));
                        customer.setAddress(documentSnapshot.getString("diaChi"));

                        // Cập nhật thông tin người dùng lên giao diện
                        txtName.setText(customer.getUsername());
                        txtPhone.setText(customer.getPhone());
                        txtLocation.setText(customer.getAddress());
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

//    Lấy dữ liệu bên giỏ hàng

    private void loadCartItems() {
        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        String khachHangID = preferences.getString("khachHangID", null);

        if (khachHangID != null) {
            loadDataUser(khachHangID);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Cart")
                    .addSnapshotListener((value, error) -> { // Sử dụng addSnapshotListener để cập nhật realtime
                        if (error != null) {
                            Toast.makeText(this, "Lỗi khi tải sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                            if (paymentDetailAdapter == null) {
                                paymentDetailAdapter = new PaymentDetailAdapter(PaymentDetailActivity.this, cartItems,
                                        PaymentDetailActivity.this::calculateCartTotal);
                                paymentDetailView.setAdapter(paymentDetailAdapter);
                            } else {
                                paymentDetailAdapter.notifyDataSetChanged();
                            }

                            calculateCartTotal();


                        }
                    });
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