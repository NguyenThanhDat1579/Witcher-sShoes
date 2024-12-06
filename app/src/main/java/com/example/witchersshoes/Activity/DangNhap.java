package com.example.witchersshoes.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.witchersshoes.R;
import com.example.witchersshoes.SendMail;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Random;

public class DangNhap extends AppCompatActivity {
    FirebaseFirestore db;
    TextInputLayout emailInputLayout, passInputLayout, emailFogotInputLayout;
    TextInputEditText edtEmail, edtPassword, edtFogotEmail;
    Button btnLogin, btnRegister;
    TextView txtFogotPass;
    CheckBox chkGhiNhoTk;
    SendMail sendMail;

    @SuppressLint("MissingInflatedId")
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
        txtFogotPass = findViewById(R.id.txtFogotPass);

        db = FirebaseFirestore.getInstance();
        sendMail = new SendMail();
        // Kiểm tra đăng nhập đã lưu
        checkSavedLoginInfo();

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

        txtFogotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
    }

    private void checkSavedLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        boolean isRemember = sharedPreferences.getBoolean("isRemember", false);

        if (isRemember) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");
            edtEmail.setText(savedEmail);
            edtPassword.setText(savedPassword);
            chkGhiNhoTk.setChecked(true);

            // Tự động đăng nhập
            checkLogin(savedEmail, savedPassword);
        }
    }



    private void checkLogin(String username, String password) {
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
                    if (!queryDocumentSnapshots.isEmpty() ) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Lấy trường 'tenKhachHang' từ tài liệu
                            String tenKhachHang = document.getString("tenKhachHang");
                            String id = document.getId(); // Lấy ID khách hàng từ Firestore
                            String diaChi = document.getString("diaChi");
                            String soDienThoai = document.getString("soDienThoai");
                            Boolean isAdmin = document.getBoolean("isAdmin");

                            if(!isAdmin) {

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
                                editor.putString("tenKhachHang", tenKhachHang);
                                editor.putString("diaChi", diaChi);
                                editor.putString("soDienThoai", soDienThoai);
//                            Toast.makeText(this, "khachHangID: "+ id, Toast.LENGTH_SHORT).show();
                                editor.apply();


                                Intent intent = new Intent(DangNhap.this, MainActivity.class);
                                intent.putExtra("tenKhachHang", tenKhachHang);
                                intent.putExtra("khachHangID", id);
                                intent.putExtra("email", username);
                                startActivity(intent);
                            }else {
                                Toast.makeText(this, "Bạn không có quyền truy cập vào trang này", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {
                        Toast.makeText(this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showCustomDialog() {
        // Tạo View từ layout custom_dialog.xml
        Dialog dialog  =  new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fogot_pass);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // Khởi tạo các phần tử trong dialog (nếu cần thiết)
        edtFogotEmail = dialog.findViewById(R.id.edtFogotEmail);
        emailFogotInputLayout = dialog.findViewById(R.id.emailFogotInputLayout);
        Button btn = dialog.findViewById(R.id.btn);
        // Ánh xạ nút btnClose
        ImageView btnClose = dialog.findViewById(R.id.btnClose);

        // Set sự kiện OnClickListener
        btnClose.setOnClickListener(v -> {
            // Đóng Dialog
            dialog.dismiss();
        });




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtFogotEmail.getText().toString().trim();


                // Kiểm tra tính hợp lệ của email
                if (email.isEmpty()) {
                    emailFogotInputLayout.setError("Email không được để trống");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailFogotInputLayout.setError("Email không hợp lệ");
                } else {
                    // Xóa lỗi nếu email hợp lệ
                    emailFogotInputLayout.setError(null);

                    btn.setEnabled(false); // Khóa nút ngay khi nhấn
                    btn.setText("Đang xử lý...");

                    // Kiểm tra email trên Firestore
                    checkIfEmailExistsInFirestore(email);

                    // Khóa nút trong 3 giây
                    new android.os.Handler().postDelayed(() -> {
                        btn.setEnabled(true); // Mở lại nút
                        btn.setText("Xác nhận"); // Trả về text ban đầu
                    }, 3000);
                }
            }
        });

        dialog.show();
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
                            sendMail.Send(DangNhap.this, email, "Xác thực", "Mã OTP là "+otp);
                            Intent intent = new Intent(DangNhap.this, OtpActivity.class);
                            intent.putExtra("otpPass", otp); // Truyền otp sang OtpActivity
                            intent.putExtra("emailFogot", email);
                            startActivity(intent);


                        } else {
                            emailFogotInputLayout.setError("Email không tồn tại"); // Xóa lỗi nếu email chưa tồn tại

                        }
                    } else {
                        Toast.makeText(DangNhap.this, "Thất bại khi kiểm tra email", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    // Hàm tạo mã OTP ngẫu nhiên 4 chữ số
    public String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);  // Tạo số ngẫu nhiên trong khoảng 1000 đến 9999
        return String.valueOf(otp);
    }

}