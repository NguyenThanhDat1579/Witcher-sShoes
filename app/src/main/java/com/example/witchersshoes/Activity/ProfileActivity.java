package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.R;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

public class ProfileActivity extends AppCompatActivity {

    TextView txtName, txtEmail;
    LinearLayout khamPha, gioHang, yeuThich, donHang, hoSo, exploreBtn, cartBtn, favoriteBtn, orderBtn, profileBtn;
    Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        txtName = findViewById(R.id.profTxtName);
        txtEmail = findViewById(R.id.profEmail);
        logoutBtn = findViewById(R.id.logoutBtn);


        khamPha = findViewById(R.id.khampha);
        gioHang = findViewById(R.id.cart);
        yeuThich = findViewById(R.id.favorite);
        donHang = findViewById(R.id.order);
        hoSo = findViewById(R.id.profile);



        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        String khachHangID = preferences.getString("khachHangID", null);

        getInfoUser(khachHangID);
        khamPha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        gioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, CartActivity.class);
                startActivity(i);
                finish();
            }
        });
        yeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, FavoriteActivity.class);
                startActivity(i);
                finish();
            }
        });
        donHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this,OrderDetailActivity.class);
                startActivity(i);
                finish();
            }
        });
        hoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo dialog xác nhận
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Clear SharedPreferences
                                SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();

                                // Chuyển về màn hình Đăng nhập
                                Intent i = new Intent(ProfileActivity.this, DangNhap.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }
                        })
                        .setPositiveButton("Không", null)
                        .show();
            }
        });

    }
    private void getInfoUser(String KhachHangID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("KhachHang")
                .document(KhachHangID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        Customer customer = new Customer();
                        customer.setId(documentSnapshot.getId());
                        customer.setUsername(documentSnapshot.getString("tenKhachHang"));
                        customer.setEmail(documentSnapshot.getString("email"));


                        txtName.setText(customer.getUsername());
                        txtEmail.setText(customer.getEmail());
                    }
                });
    }
}