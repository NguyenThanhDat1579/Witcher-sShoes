package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.OrderDetailAdapter;
import com.example.witchersshoes.Model.Order;
import com.example.witchersshoes.Model.Product;
import com.example.witchersshoes.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderDetailAdapter orderAdapter;
    private List<Order> ordersList;
    private FirebaseFirestore db;
    private String khachHangID;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        rvOrders = findViewById(R.id.rvOrders);
        btnBack = findViewById(R.id.backBtn);
        ordersList = new ArrayList<>();
        orderAdapter = new OrderDetailAdapter(this, ordersList);
        rvOrders.setAdapter(orderAdapter);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));


        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        khachHangID = preferences.getString("khachHangID", null);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loadOrders();
    }

    public void loadOrders() {
        if (khachHangID == null) return;

        db = FirebaseFirestore.getInstance();
        db.collection("KhachHang")
                .document(khachHangID)
                .collection("Orders")
                .orderBy("createdAt", Query.Direction.DESCENDING) // Sắp xếp theo thời gian mới nhất
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi khi tải đơn hàng: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        ordersList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Order order = new Order();
                            order.setOrderID(doc.getId());
                            order.setCreatedAt(doc.getTimestamp("createdAt").toDate());
                            order.setStatus(doc.getString("status"));
                            order.setTotalAmount(doc.getDouble("totalAmount"));

                            // Lấy danh sách sản phẩm
                            List<Map<String, Object>> productsData = (List<Map<String, Object>>) doc.get("products");
                            if (productsData != null) {
                                for (Map<String, Object> productData : productsData) {
                                    Product product = new Product();
                                    product.setId((String) productData.get("productID"));
                                    product.setProductName((String) productData.get("productName"));
                                    product.setQuantity(((Long) productData.get("quantity")).intValue());
                                    product.setPrice(String.valueOf(productData.get("price")));
                                    Object imageObj = productData.get("imageUrl");
                                    product.setImage(imageObj instanceof ArrayList ?
                                            String.valueOf(((ArrayList<?>) imageObj).get(0)) :
                                            String.valueOf(imageObj));
                                    order.getProducts().add(product);
                                }
                            }

                            ordersList.add(order);
                        }
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        TextView emptyTxt = findViewById(R.id.emptyTxt);
        RecyclerView rvOrders = findViewById(R.id.rvOrders);

        if (ordersList.isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            emptyTxt.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
        }
        orderAdapter.notifyDataSetChanged();
    }

}

