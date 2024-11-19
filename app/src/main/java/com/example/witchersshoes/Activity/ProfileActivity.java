package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.R;
import com.example.witchersshoes.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {
    TextView txtName, txtEmail;
    LinearLayout khamPha, gioHang, yeuThich, donHang, hoSo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        txtName = findViewById(R.id.profTxtName);
        txtEmail = findViewById(R.id.profEmail);


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
            }
        });
        gioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, CartActivity.class);
                startActivity(i);
            }
        });
        yeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, FavoriteActivity.class);
                startActivity(i);
            }
        });
        donHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this,OrderDetailActivity.class);
                startActivity(i);
            }
        });
        hoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        private void bottomNavigation() {
            binding.cartBtn.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, CartActivity.class));

            });
            binding.favoriteBtn.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
            });

            binding.orderBtn.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, OrderDetailActivity.class));
            });
            binding.profileBtn.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            });
        }
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