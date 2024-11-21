package com.example.witchersshoes.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.BestSellerAdapter;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class CategoryActivity extends AppCompatActivity {
    RecyclerView rcv;
    ImageView backBtn;
    TextView titleProCatTxt;
    ProgressBar progressBarCatDetail;
    BestSellerAdapter bestSellerAdapter;
    List<ProductModel> category1List = new ArrayList<>();
    List<ProductModel> filterList = new ArrayList<>();
    FirebaseFirestore firestore;
    private MutableLiveData<List<ProductModel>> _bestSeller = new MutableLiveData<>();
    public LiveData<List<ProductModel>> getBestSeller() {
        return _bestSeller;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_category);

        firestore = FirebaseFirestore.getInstance();

        backBtn = findViewById(R.id.backBtn);
        progressBarCatDetail = findViewById(R.id.progressBarCatDetail);
        titleProCatTxt = findViewById(R.id.titleProCatTxt);

        // Khởi tạo RecyclerView và LayoutManager
        rcv = findViewById(R.id.category1_recyclerView);
        rcv.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo Adapter với danh sách trống và gắn vào RecyclerView
        bestSellerAdapter = new BestSellerAdapter(filterList);
        rcv.setAdapter(bestSellerAdapter);
        progressBarCatDetail.setVisibility(View.GONE);

        // Gọi phương thức để tải dữ liệu
        loadBestSeller();

        setVariable();

    }


    public void loadBestSeller() {
        CollectionReference ref = firestore.collection("Products");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore Error", error.getMessage());
                    return;
                }

                filterList.clear();
                String categoryID = getIntent().getStringExtra("id");
                String title = getIntent().getStringExtra("title");
                titleProCatTxt.setText(title);
                for (QueryDocumentSnapshot document : snapshot) {
                    try {
                        // Chuyển dữ liệu từ document sang ProductModel
                        ProductModel list = document.toObject(ProductModel.class);

                        // Lấy DocumentReference từ document
                        DocumentReference documentCategoryRef = document.getDocumentReference("categoryID"); // Thay "categoryID" bằng tên thực tế của trường trong Firestore

                        if (documentCategoryRef != null) {
                            String documentCategoryId = documentCategoryRef.getId(); // Lấy ID của DocumentReference
                            String documentId = document.getId();
                            Log.d("category", "Document Category ID: " + documentCategoryId);
                            list.setID(documentId);
                            if (categoryID != null && categoryID.equals(documentCategoryId)) {
                                filterList.add(list);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Data Parsing Error", e.getMessage());
                    }
                }
                bestSellerAdapter.notifyDataSetChanged();
            }
        });
    }


    private void setVariable() {
        backBtn.setOnClickListener(v -> startActivity(new Intent(CategoryActivity.this, MainActivity.class)));

    }

}
