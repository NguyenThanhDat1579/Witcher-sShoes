package com.example.witchersshoes.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.R;
import com.example.witchersshoes.Adapter.Category1Adapter;
import com.example.witchersshoes.Model.ProductCategory1;

import java.util.ArrayList;
import java.util.List;

public class Category1 extends AppCompatActivity {
    RecyclerView rcv;
    Category1Adapter category1Adapter;
    List<ProductCategory1> category1List;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rcv = findViewById(R.id.category1_recyclerView);
        rcv.setLayoutManager(new GridLayoutManager(this, 2));
        category1List = new ArrayList<>();
        category1List.add(new ProductCategory1("si dep trai", 2500000, R.drawable.cake1_1));
        category1List.add(new ProductCategory1("si xinh trai", 1800000, R.drawable.cake1_2));
        category1List.add(new ProductCategory1("si hot boy", 1800000, R.drawable.cake1_3));

        category1Adapter = new Category1Adapter(category1List);
        rcv.setAdapter(category1Adapter);
    }
}