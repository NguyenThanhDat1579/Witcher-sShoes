// FavoriteActivity.java
package com.example.witchersshoes.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.example.witchersshoes.Adapter.BestSellerAdapter;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.databinding.ActivityFavoriteBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends BaseActivity {
    private ActivityFavoriteBinding binding;
    private FirebaseFirestore db;
    private String khachHangID;
    private List<ProductModel> favoriteItems;
    private BestSellerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo
        db = FirebaseFirestore.getInstance();
        favoriteItems = new ArrayList<>();

        // Lấy khachHangID từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("THONGTIN", Context.MODE_PRIVATE);
        khachHangID = preferences.getString("khachHangID", null);

        // Setup RecyclerView
        binding.recyclerViewFavorite.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BestSellerAdapter(favoriteItems, true); // true để chỉ định đây là favorite list
        binding.recyclerViewFavorite.setAdapter(adapter);

        // Load favorite items
        loadFavoriteItems();

        // Back button
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void loadFavoriteItems() {
        if (khachHangID == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("KhachHang")
                .document(khachHangID)
                .collection("Favorite")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteItems.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ProductModel product = document.toObject(ProductModel.class);
                        product.setID(document.getId());
                        favoriteItems.add(product);
                    }
                    adapter.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);

                    // Hiển thị message nếu không có sản phẩm yêu thích
                    if (favoriteItems.isEmpty()) {
                        binding.emptyTxt.setVisibility(View.VISIBLE);
                    } else {
                        binding.emptyTxt.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải sản phẩm yêu thích: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                });
    }
}