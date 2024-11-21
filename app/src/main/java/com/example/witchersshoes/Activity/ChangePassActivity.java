package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChangePassActivity extends AppCompatActivity {
    TextInputEditText edtPassword,edtRePassword;
    Button btnChange;
    TextView textView3;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnChange = findViewById(R.id.btnChange);
        textView3 = findViewById(R.id.textView3);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Intent intent  = getIntent();
        String email = intent.getStringExtra("email");

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = edtPassword.getText().toString().trim();
                String rePass = edtRePassword.getText().toString().trim();
                // Kiểm tra mật khẩu
                if (newPass.isEmpty()) {
                    edtPassword.setError("Mật khẩu không được để trống");
                    edtPassword.requestFocus();
                    return;
                }


                if (!newPass.equals(rePass)) {
                    edtRePassword.setError("Mật khẩu nhập lại không khớp");
                    edtRePassword.requestFocus();
                    return;
                }

                // Nếu tất cả kiểm tra đều vượt qua, tiến hành cập nhật mật khẩu
                updatePassword(email, newPass);
                startActivity(new Intent(ChangePassActivity.this, DangNhap.class));
                finish();
            }

        });
    }
    private void updatePassword(String email, String newPassword) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tìm tài liệu dựa theo email
        db.collection("KhachHang")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Lấy tài liệu đầu tiên khớp với email
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();

                            // Cập nhật mật khẩu mới
                            db.collection("KhachHang")
                                    .document(documentId)
                                    .update("matKhau", newPassword)
                                    .addOnSuccessListener(aVoid -> {

                                        Toast.makeText(this, "Mật khẩu đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(this, "Lỗi khi cập nhật mật khẩu", Toast.LENGTH_SHORT).show();
                                    });

                            // Chỉ cần cập nhật tài liệu đầu tiên, nên dừng vòng lặp
                            break;
                        }
                    } else {

                        Toast.makeText(this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Lỗi khi tìm kiếm email", Toast.LENGTH_SHORT).show();
                });
    }

}