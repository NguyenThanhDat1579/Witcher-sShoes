package com.example.witchersshoes.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.witchersshoes.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DangNhap extends AppCompatActivity {
    FirebaseFirestore db;
    TextInputLayout emailInputLayout, passInputLayout;
    TextInputEditText edtEmail, edtPassword;
    Button btnLogin, btnRegister;
    CheckBox chkGhiNhoTk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);

        emailInputLayout = findViewById(R.id.emailInputLayout);
        passInputLayout = findViewById(R.id.passInputLayout);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        chkGhiNhoTk = findViewById(R.id.chkGhiNhoTk);

        db = FirebaseFirestore.getInstance();

        // Kiểm tra nếu có thông tin ghi nhớ tài khoản
        loadSavedLoginInfo();

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            // Kiểm tra email và mật khẩu
            boolean isValid = true;

            // Kiểm tra email
            if (email.isEmpty()) {
                emailInputLayout.setError("Email không được để trống");
                isValid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.setError("Email không hợp lệ");
                isValid = false;
            } else {
                emailInputLayout.setError(null); // Xóa lỗi nếu email hợp lệ
            }
            if (!email.isEmpty() && !password.isEmpty()) {
                checkLogin(email, password);
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangNhap.this, DangKy.class);
                startActivity(intent);
            }
        });
    }

    private void loadSavedLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        boolean isRemember = sharedPreferences.getBoolean("isRemember", false);

        if (isRemember) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");
            edtEmail.setText(savedEmail);
            edtPassword.setText(savedPassword);
            chkGhiNhoTk.setChecked(true);
        }
    }

    private void checkLogin(String username, String password){
        // Tạo ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang kiểm tra thông tin đăng nhập...");
        progressDialog.setCancelable(false); // Ngăn người dùng tắt dialog bằng cách bấm bên ngoài
        progressDialog.show(); // Hiển thị dialog


        db.collection("KhachHang")
                .whereEqualTo("email", username)
                .whereEqualTo("matKhau", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Ẩn dialog khi có kết quả
                    progressDialog.dismiss();
                    if(!queryDocumentSnapshots.isEmpty()){
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Lấy trường 'tenKhachHang' từ tài liệu
                            String tenKhachHang = document.getString("tenKhachHang");
                            String id = document.getId(); // Lấy ID khách hàng từ Firestore

                            // Lưu thông tin đăng nhập vào SharedPreferences nếu checkbox được chọn
                            SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();

                            // Lưu thông tin đăng nhập
                            if (chkGhiNhoTk.isChecked()) {
                                editor.putBoolean("isRemember", true);
                                editor.putString("email", username);
                                editor.putString("password", password);
                            } else {
                                editor.putBoolean("isRemember", false);
                                editor.remove("email");
                                editor.remove("password");
                            }
                            editor.putString("khachHangID", id); // Lưu ID khách hàng
//                            Toast.makeText(this, "khachHangID: "+ id, Toast.LENGTH_SHORT).show();
                            editor.apply();



                            Intent intent = new Intent(DangNhap.this, MainActivity.class);
                            intent.putExtra("tenKhachHang", tenKhachHang);
                            intent.putExtra("khachHangID", id);
                            startActivity(intent);
                        }
                    }
                    else{
                        Toast.makeText(this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}