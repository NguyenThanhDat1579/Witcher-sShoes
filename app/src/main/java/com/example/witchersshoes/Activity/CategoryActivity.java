package com.example.witchersshoes.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

     EditText edtSearchCategories;

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

        edtSearchCategories = findViewById(R.id.edtSearchCategories);

        // Khởi tạo RecyclerView và LayoutManager
        rcv = findViewById(R.id.category1_recyclerView);
        rcv.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo Adapter với danh sách trống và gắn vào RecyclerView
        bestSellerAdapter = new BestSellerAdapter(filterList);
        rcv.setAdapter(bestSellerAdapter);
        progressBarCatDetail.setVisibility(View.GONE);

        // Gọi phương thức để tải dữ liệu
        loadBestSeller();

        // Thiết lập TextWatcher cho edtSearchCategories
        edtSearchCategories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



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

                category1List.clear(); // Đặt lại danh sách chính
                filterList.clear();    // Đặt lại danh sách hiển thị
                String categoryID = getIntent().getStringExtra("id");
                String title = getIntent().getStringExtra("title");
                titleProCatTxt.setText(title);

                for (QueryDocumentSnapshot document : snapshot) {
                    try {
                        ProductModel list = document.toObject(ProductModel.class);
                        DocumentReference documentCategoryRef = document.getDocumentReference("categoryID");

                        if (documentCategoryRef != null) {
                            String documentCategoryId = documentCategoryRef.getId();
                            String documentId = document.getId();
                            Log.d("category", "Document Category ID: " + documentCategoryId);
                            list.setID(documentId);
                            if (categoryID != null && categoryID.equals(documentCategoryId)) {
                                category1List.add(list); // Thêm vào danh sách chính
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Data Parsing Error", e.getMessage());
                    }
                }
                filterList.addAll(category1List); // Hiển thị toàn bộ danh sách ban đầu
                bestSellerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void filterProducts(String query) {
        filterList.clear();
        if (query.isEmpty()) {
            filterList.addAll(category1List); // Nếu không có từ khóa, hiển thị tất cả sản phẩm
        } else {
            for (ProductModel product : category1List) {
                if (product.getTitle() != null && product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filterList.add(product);
                }
            }
        }

        if (filterList.isEmpty()) {
            // Hiển thị thông báo bằng Toast nếu không tìm thấy sản phẩm
            Toast.makeText(this, "Không tìm thấy sản phẩm phù hợp!", Toast.LENGTH_SHORT).show();
        }
        bestSellerAdapter.notifyDataSetChanged();
    }


    private void setVariable() {
        backBtn.setOnClickListener(v -> startActivity(new Intent(CategoryActivity.this, MainActivity.class)));

    }

}
