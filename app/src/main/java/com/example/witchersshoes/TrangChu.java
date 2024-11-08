package com.example.witchersshoes;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.adapter.ProductAdapter;
import com.example.witchersshoes.classes.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrangChu extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<Product> products = new ArrayList<>();

    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_chu);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        // Cài đặt LayoutManager và Adapter cho RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 cột

        adapter = new ProductAdapter(products);
        recyclerView.setAdapter(adapter);

        loadDataFromFireStore();
    }
    private void loadDataFromFireStore(){
        db.collection("Products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    products.clear();
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                        //Chuyển đổi documon thành 1 object
                        Product product = snapshot.toObject(Product.class);
                        products.add(product);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}