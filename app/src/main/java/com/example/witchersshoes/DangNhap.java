package com.example.witchersshoes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DangNhap extends AppCompatActivity {
    FirebaseFirestore db;
    EditText edtUsername, edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            if (!username.isEmpty() && !password.isEmpty()) {
                checkLogin(username, password);
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Ham lay du lieu tu Firestore
    private void nguoiDung(String userID){
        db.collection("KhachHang").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        String username = documentSnapshot.getString("tenDangNhap");
                        String password = documentSnapshot.getString("matKhau");
                    }
                    else{
                        Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkLogin(String username, String password){
        db.collection("KhachHang")
                .whereEqualTo("tenDangNhap", username)
                .whereEqualTo("matKhau", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String documentID = document.getId();
                            Toast.makeText(this, "Login successful, Document ID: " + documentID, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(this, "Dang nhap that bai", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}