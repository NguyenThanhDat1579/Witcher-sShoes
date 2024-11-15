package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.witchersshoes.R;
import com.example.witchersshoes.SendMail;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Random;

public class FogotPassActivity extends AppCompatActivity {
    TextInputEditText edtEmail;
    TextInputLayout emailInputLayout;
    Button btn;
    FirebaseFirestore db;
    SendMail sendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fogot_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtEmail = findViewById(R.id.edtEmail);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        btn = findViewById(R.id.btn);
        db = FirebaseFirestore.getInstance();
        sendMail = new SendMail();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                checkIfEmailExistsInFirestore(email);
            }
        });
    }

    public void checkIfEmailExistsInFirestore(String email) {
        db.collection("KhachHang")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean emailExists = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            emailExists = true;
                            break;
                        }
                        if (emailExists) {
                            String otp = generateOtp();
                            Intent intent = new Intent(FogotPassActivity.this, OtpActivity.class);
                            intent.putExtra("otpPass", otp); // Truyền otp sang OtpActivity
                            startActivity(intent);
                        } else {
                            emailInputLayout.setError("Email không tồn tại"); // Xóa lỗi nếu email chưa tồn tại

                        }
                    } else {
                        Toast.makeText(FogotPassActivity.this, "Thất bại khi kiểm tra email", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Hàm tạo mã OTP ngẫu nhiên 6 chữ số
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Tạo số ngẫu nhiên trong khoảng 100000 đến 999999
        return String.valueOf(otp);
    }
}