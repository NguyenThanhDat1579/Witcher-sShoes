package com.example.witchersshoes;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.classes.ToDo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore database;
    TextView txtNameShoes, txtSize, txtCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        txtNameShoes = findViewById(R.id.shoesName);
        txtSize = findViewById(R.id.size);
        txtCategory = findViewById(R.id.category);

        // Khởi tạo FirebaseFirestore
        database = FirebaseFirestore.getInstance();


        String id = UUID.randomUUID().toString();
        String title = "tieu de ne";
        String content = "noi dung ne";

        ToDo toDo = new ToDo(id, title, content);
        HashMap<String, Object> map = toDo.convertHashMap();
        //push du lieu len
        database.collection("ToDo").document(id)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "thanh cong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "that bai", Toast.LENGTH_SHORT).show();
                    }
                });
/*
        //lay du lieu ve
        database.collection("ToDo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển dữ liệu thành một đối tượng ToDo hoặc xử lý tùy ý
                            ToDo toDo2 = document.toObject(ToDo.class);
                            String title2 = toDo.getTitle();
                            String content2 = toDo.getContent();

                            txtNameShoes.setText(title2);
                            // Hiển thị dữ liệu
                            Toast.makeText(MainActivity.this, "Title: " + title2 + ", Content: " + content2, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
*/
        database.collection("Shoes").document("s1")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                   if(documentSnapshot.exists()){
                       String name = documentSnapshot.getString("name");
                       String size = documentSnapshot.getString("size");
                       // Liên kết với loại giày
                       DocumentReference typeRef = documentSnapshot.getDocumentReference("category");

                       txtNameShoes.setText(name);
                       txtSize.setText(size);
                       if(typeRef!=null){
                           typeRef.get().addOnSuccessListener(typeSnapshot->{
                               if(typeSnapshot.exists()){
                                   String category = typeSnapshot.getString("name");
                                   txtNameShoes.setText(name);
                                   txtSize.setText(size);
                                   txtCategory.setText(category);
                               }
                           });
                       }
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting shoe data", e));

    }
}
