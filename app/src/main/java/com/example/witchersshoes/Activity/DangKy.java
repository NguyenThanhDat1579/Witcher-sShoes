package com.example.witchersshoes.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.witchersshoes.R;
import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.SendMail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DangKy extends AppCompatActivity {

    FirebaseFirestore db;
    TextInputLayout nameInputLayout, emailInputLayout, passInputLayout, rePassInputLayout;
    EditText edtUsername, edtEmail, edtPassword, edtRePassword;
    Button btnRegister, btnLogin;
    SendMail sendMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);
        init();
        sendMail = new SendMail();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRegister();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangKy.this, DangNhap.class);
                startActivity(intent);
            }
        });


    }
    public void checkRegister(){
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String rePassword = edtRePassword.getText().toString().trim();



        // Kiểm tra tên người dùng
        if (username.isEmpty()) {
            nameInputLayout.setError("Tên không được để trống");
        } else {
            nameInputLayout.setError(null); // Xóa lỗi nếu tên hợp lệ
        }

        // Kiểm tra email
        if (email.isEmpty()) {
            emailInputLayout.setError("Email không được để trống");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Email không hợp lệ");
        } else if (!email.endsWith("@gmail.com")) {
            emailInputLayout.setError("Email phải có định dạng @gmail.com");
        } else {
            emailInputLayout.setError(null); // Xóa lỗi nếu email hợp lệ
        }

        // Kiểm tra mật khẩu
        if (password.isEmpty()) {
            passInputLayout.setError("Mật khẩu không được để trống");
        } else {
            passInputLayout.setError(null); // Xóa lỗi nếu mật khẩu hợp lệ
        }

        // Kiểm tra lại mật khẩu
        if (rePassword.isEmpty()) {
            rePassInputLayout.setError("Vui lòng nhập lại mật khẩu");
        } else if (!password.equals(rePassword)) {
            rePassInputLayout.setError("Mật khẩu không trùng khớp");
        } else {
            rePassInputLayout.setError(null); // Xóa lỗi nếu mật khẩu trùng khớp
        }

        // Nếu tất cả thông tin hợp lệ, tiến hành kiểm tra email trong Firestore
        if (isValid(username, email, password, rePassword)) {
            checkIfEmailExistsInFirestore(email, password, username);
        }


//            Customer customer = new Customer(id,  email, username, password);
//            HashMap<String, Object> customers = customer.convertHashMap();
//            //push du lieu len
//            db.collection("KhachHang").document(id)
//                    .set(customers)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            progressDialog.dismiss(); // Tắt loading khi thành công
//                            Toast.makeText(DangKy.this, "thanh cong", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(DangKy.this, DangNhap.class);
//                            startActivity(intent);
//                            finishAffinity();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss(); // Tắt loading khi thành công
//                            Toast.makeText(DangKy.this, "that bai", Toast.LENGTH_SHORT).show();
//                        }
//                    });

    }

    private boolean isValid(String username, String email, String password, String rePassword) {
        // Kiểm tra xem tất cả các trường hợp có hợp lệ không
        return !username.isEmpty() &&
                !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@gmail.com") &&
                !password.isEmpty() && !rePassword.isEmpty() && password.equals(rePassword);
    }

    public void checkIfEmailExistsInFirestore(String email, String pass, String name) {
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
                            emailInputLayout.setError("Email đã tồn tại");
                        } else {
                            emailInputLayout.setError(null); // Xóa lỗi nếu email chưa tồn tại
                            String otp = generateOtp();
                            // Chuyển sang OtpActivity nếu email chưa tồn tại
                            sendMail.Send(DangKy.this, email, "Xác thực", "Mã OTP là "+otp);
                            Intent intent = new Intent(DangKy.this, OtpActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("email", email); // Truyền email sang OtpActivity
                            intent.putExtra("otp", otp); // Truyền otp sang OtpActivity
                            intent.putExtra("pass", pass); // Truyền pass sang OtpActivity
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(DangKy.this, "Thất bại khi kiểm tra email", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Hàm tạo mã OTP ngẫu nhiên 6 chữ số
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Tạo số ngẫu nhiên trong khoảng 100000 đến 999999
        return String.valueOf(otp);
    }


    public void init(){
        nameInputLayout = findViewById(R.id.namelInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passInputLayout = findViewById(R.id.passInputLayout);
        rePassInputLayout = findViewById(R.id.rePassInputLayout);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        db = FirebaseFirestore.getInstance();
    }

}