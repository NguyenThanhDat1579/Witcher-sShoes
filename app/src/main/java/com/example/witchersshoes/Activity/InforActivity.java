package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InforActivity extends AppCompatActivity {

    private EditText usernameEdt, addressEdt, phoneEdt;
    private Button editBtn, saveBtn, cancelBtn;
    private ImageView backBtn;

    private FirebaseFirestore db;
    private String customerId = "CUSTOMER_ID"; // Thay bằng ID khách hàng thực tế

    // Biến lưu trữ dữ liệu ban đầu
    private String initialUsername;
    private String initialAddress;
    private String initialPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor);

        // Lấy dữ liệu tenKhachHang từ Intent
        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        String khachHangID = preferences.getString("khachHangID", null);

        // Ánh xạ view
        usernameEdt = findViewById(R.id.usernameEdt);
        addressEdt = findViewById(R.id.addressEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        editBtn = findViewById(R.id.editBtn);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        backBtn = findViewById(R.id.backBtn);

        setVariable();

        // Khóa các EditText ban đầu
        setEditable(false);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu người dùng
        getInfoUser(khachHangID);


        // Xử lý sự kiện chỉnh sửa
        editBtn.setOnClickListener(v -> {
            setEditable(true);
            editBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        });

        // Lưu dữ liệu
        saveBtn.setOnClickListener(v -> saveCustomerData());

        // Hủy chỉnh sửa
        cancelBtn.setOnClickListener(v -> {
            setEditable(false);
            editBtn.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            getInfoUser(khachHangID); // Reload dữ liệu
        });
    }

    private void getInfoUser(String khachHangID) {
        db.collection("KhachHang")
                .document(khachHangID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Customer customer = new Customer();
                        customer.setId(documentSnapshot.getId());
                        customer.setUsername(documentSnapshot.getString("tenKhachHang"));
                        customer.setAddress(documentSnapshot.getString("diaChi"));
                        customer.setPhone(documentSnapshot.getString("soDienThoai"));

                        // Đổ dữ liệu vào EditText
                        usernameEdt.setText(customer.getUsername());
                        addressEdt.setText(customer.getAddress());
                        phoneEdt.setText(customer.getPhone());
                    } else {
                        Log.e("InforActivity", "Document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("InforActivity", "Failed to fetch user info: " + e.getMessage()));
    }

    private void saveCustomerData() {
        String username = usernameEdt.getText().toString().trim();
        String address = addressEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();

        // Lấy dữ liệu tenKhachHang từ Intent
        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        String khachHangID = preferences.getString("khachHangID", null);

        // Cập nhật dữ liệu lên Firestore
        db.collection("KhachHang")
                .document(khachHangID)
                .update("tenKhachHang", username, "diaChi", address, "soDienThoai", phone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setEditable(false);
                        editBtn.setVisibility(View.VISIBLE);
                        saveBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);
                        Toast.makeText(this, "Sửa thông tin thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("InforActivity", "Failed to update customer data");
                    }
                });
    }

    private void setEditable(boolean isEditable) {
        usernameEdt.setEnabled(isEditable);
        addressEdt.setEnabled(isEditable);
        phoneEdt.setEnabled(isEditable);
    }

    private void setVariable() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InforActivity.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
