package com.example.witchersshoes.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.BestSellerAdapter;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.R;
import com.example.witchersshoes.Adapter.Category1Adapter;
import com.example.witchersshoes.Model.ProductCategory1;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class Category1 extends AppCompatActivity {
    RecyclerView rcv;
    BestSellerAdapter bestSellerAdapter;
    List<ProductModel> category1List = new ArrayList<>();
    List<ProductModel> filterList = new ArrayList<>();
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1);

        firestore = FirebaseFirestore.getInstance();

        // Khởi tạo RecyclerView và LayoutManager
        rcv = findViewById(R.id.category1_recyclerView);
        rcv.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo Adapter với danh sách trống và gắn vào RecyclerView
        bestSellerAdapter = new BestSellerAdapter(filterList);
        rcv.setAdapter(bestSellerAdapter);

        // Gọi phương thức để tải dữ liệu
        loadBestSeller();
    }

    public void loadBestSeller() {
        CollectionReference ref = firestore.collection("Products");
        ref.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.e("Firestore Error", error.getMessage());
                return;
            }

            // Xóa các danh sách cũ để làm mới dữ liệu
            filterList.clear();

            // Lấy giá trị ID từ Intent
            String categoryID = getIntent().getStringExtra("id");

            // Lặp qua từng document trong snapshot
            for (QueryDocumentSnapshot document : snapshot) {
                try {
                    // Chuyển đổi document thành ProductModel
                    ProductModel productModel = document.toObject(ProductModel.class);
                    String documentId = document.getId();
                    productModel.setID(documentId); // Lưu ID của document

                    // Kiểm tra điều kiện thêm vào filterList
                    if (categoryID != null && categoryID.equals(documentId)) {
                        filterList.add(productModel);
                    }
                } catch (Exception e) {
                    Log.e("Data Parsing Error", e.getMessage());
                }
            }

            // Cập nhật Adapter sau khi đã thêm toàn bộ dữ liệu vào filterList
            bestSellerAdapter.notifyDataSetChanged();
        });
    }
}
