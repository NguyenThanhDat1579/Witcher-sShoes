package com.example.witchersshoes;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    FirebaseFirestore db;
    TextView txtTenGiay, txtSizeGiay, txtLoaiGiay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        txtTenGiay = findViewById(R.id.tenGiay);
        txtSizeGiay = findViewById(R.id.sizeGiay);
        txtLoaiGiay = findViewById(R.id.loaiGiay);

        db = FirebaseFirestore.getInstance();

        // Thêm loại giày mới
        Map<String, Object> shoesTypes = new HashMap<>();
        shoesTypes.put("tenLoaiGiay", "Nam");
        db.collection("loai_giay").add(shoesTypes);

        // Tham chiếu đến một document trong collection giày
        DocumentReference typesRef = db.collection("loai_giay").document("l1");

        // Lưu tham chiếu này vào một document trong collection giày
        Map<String, Object> shoesData = new HashMap<>();
        shoesData.put("tenGiay", "Sneakers");
        shoesData.put("size", "42");
        shoesData.put("loaiGiay", typesRef);

        db.collection("giay").document("l1").set(shoesData);

        // Lấy dữ liệu giày
        db.collection("giay").document("idGiay")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("tenGiay");
                        String size = documentSnapshot.getString("size");
                        DocumentReference typeRef = documentSnapshot.getDocumentReference("loaiGiay");

                        if (typeRef != null) {
                            typeRef.get().addOnSuccessListener(typeSnapshot -> {
                                String typeName = typeSnapshot.getString("tenLoaiGiay");
                                txtTenGiay.setText(name);
                                txtSizeGiay.setText(size);
                                txtLoaiGiay.setText(typeName);
                            });
                        } else {
                            txtLoaiGiay.setText("Không có loại giày");
                        }
                    } else {
                        Toast.makeText(MainActivity2.this, "Không tìm thấy giày", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}