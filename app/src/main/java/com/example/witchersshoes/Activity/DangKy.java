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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class DangKy extends AppCompatActivity {

    FirebaseFirestore db;
    TextInputLayout namelInputLayout, emailInputLayout, phoneInputLayout, addressInputLayout, passInputLayout, rePassInputLayout;
    EditText edtUsername, edtEmail,edtPhone,edtAddress, edtPassword, edtRePassword;
    Button btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ky);
        init();


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
        String id = UUID.randomUUID().toString();
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String rePassword = edtRePassword.getText().toString().trim();


        // Kiểm tra
        boolean isValid = true;

        // Kiểm tra name
        if(username.isEmpty()){
            namelInputLayout.setError("Tên không được để trống");
            isValid = false;
        }else{
            namelInputLayout.setError(null);
        }

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

        if (phone.isEmpty()) {
            phoneInputLayout.setError("Số điện thoại không được để trống");
            isValid = false;
        } else if (!Patterns.PHONE.matcher(phone).matches() || phone.length() < 10 || phone.length() > 11) {
            phoneInputLayout.setError("Số điện thoại không hợp lệ");
            isValid = false;
        } else if (!phone.startsWith("03") && !phone.startsWith("07") && !phone.startsWith("08")
                && !phone.startsWith("09") && !phone.startsWith("01")) {
            phoneInputLayout.setError("Số điện thoại không đúng đầu số nhà mạng");
            isValid = false;
        } else {
            phoneInputLayout.setError(null); // Xóa lỗi nếu hợp lệ
        }

        // Kiểm tra Address
        if(address.isEmpty()){
            addressInputLayout.setError("Địa chỉ không được để trống");
            isValid = false;
        }else{
            addressInputLayout.setError(null);
        }

        // Kiểm tra mật khẩu
        if (password.isEmpty() ) {
            passInputLayout.setError("Mật khẩu không được để trống");
            isValid = false;
        } else {
            passInputLayout.setError(null); // Xóa lỗi nếu mật khẩu hợp lệ
        }
        // Kiểm tra lại mật khẩu

        if (rePassword.isEmpty()) {
            rePassInputLayout.setError("Vui lòng nhập lại mật khẩu");
            isValid = false;
        } else if (!password.equals(rePassword)) {
            rePassInputLayout.setError("Mật khẩu không trùng khớp");
            isValid = false;
        } else {
            rePassInputLayout.setError(null); // Xóa lỗi nếu mật khẩu trùng khớp
        }


        // Nếu tất cả đều hợp lệ, thực hiện đăng ký
        if (isValid) {

            // Tạo ProgressDialog
            ProgressDialog progressDialog = new ProgressDialog(DangKy.this);
            progressDialog.setMessage("Đang đăng ký...");
            progressDialog.setCancelable(false); // Người dùng không thể huỷ bỏ khi đang load
            progressDialog.show();



            Customer customer = new Customer(id,  email, username, password, phone, address);
            HashMap<String, Object> customers = customer.convertHashMap();
            //push du lieu len
            db.collection("KhachHang").document(id)
                    .set(customers)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss(); // Tắt loading khi thành công
                            Toast.makeText(DangKy.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DangKy.this, DangNhap.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss(); // Tắt loading khi thành công
                            Toast.makeText(DangKy.this, "that bai", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        }




    public void init(){
        namelInputLayout = findViewById(R.id.namelInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        addressInputLayout = findViewById(R.id.addressInputLayout);
        passInputLayout = findViewById(R.id.passInputLayout);
        rePassInputLayout = findViewById(R.id.rePassInputLayout);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        db = FirebaseFirestore.getInstance();
    }

}