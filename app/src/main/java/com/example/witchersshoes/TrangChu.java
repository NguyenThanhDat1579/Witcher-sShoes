package com.example.witchersshoes;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.classes.Product;

import java.util.ArrayList;
import java.util.List;

public class TrangChu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_chu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Tạo danh sách sản phẩm mẫu
        List<Product> products = new ArrayList<>();
        products.add(new Product("Basas Workaday", "₫2,500,000", R.drawable.shoes1));
        products.add(new Product("Basas RAW", "₫1,800,000", R.drawable.shoes3));
        products.add(new Product("Basas Evergreen", "₫2,500,000", R.drawable.shoes4));
        products.add(new Product("Basas Workaday", "₫2,500,000", R.drawable.shoes1));

        // Cài đặt LayoutManager và Adapter cho RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 cột
        ProductAdapter adapter = new ProductAdapter(products);
        recyclerView.setAdapter(adapter);
    }
}