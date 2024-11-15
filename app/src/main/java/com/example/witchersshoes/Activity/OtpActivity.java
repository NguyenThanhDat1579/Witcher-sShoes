package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {
    TextView txtEmail;
    EditText edtOtp;
    Button btnSubmit;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtEmail = findViewById(R.id.txtEmail);
        edtOtp = findViewById(R.id.edtOtp);
        btnSubmit = findViewById(R.id.btnSubmit);
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String username = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String otp = intent.getStringExtra("otp");
        String pass = intent.getStringExtra("pass");

        txtEmail.setText(email);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtOtp.getText().toString().equals(otp)){
                    Toast.makeText(OtpActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                    // Tạo tài khoản người dùng mới trên Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(OtpActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // Lấy thông tin người dùng
                                    String userId = auth.getCurrentUser().getUid();

                                    // Lưu thông tin người dùng vào Firestore
                                    Customer customer  = new Customer(userId, email, username, pass);
                                    HashMap<String, Object> customers = customer.convertHashMap();

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("KhachHang").document(userId)
                                            .set(customers)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(OtpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                // Chuyển sang màn hình chính và truyền tên người dùng
                                                Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                                                intent.putExtra("name", username); // Thêm tên vào Intent
                                                startActivity(intent);
                                                finish();  // Đóng Activity hiện tại
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(OtpActivity.this, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(OtpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(OtpActivity.this, "OTP không chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}