package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.witchersshoes.Adapter.PicListAdapter;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private ProductModel item;
    private int numberOrder = 1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        getBundleExtra();
        initLists();


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);

    }

    private void initLists() {

        ArrayList<String> picList = new ArrayList<>(item.getPicUrl());

        Glide.with(this)
                .load(picList.get(0))
                .into(binding.picMain);


        binding.picList.setAdapter(new PicListAdapter(picList, binding.picMain));
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void getBundleExtra() {
        item = (ProductModel) getIntent().getSerializableExtra("object");

        binding.titleTxt.setText(item.getTitle());
        binding.desciptionTxt.setText(item.getDescription());
        binding.priceTxt.setText(item.getPrice()+"00₫");
        binding.ratingTxt.setText(item.getRating() + " ");
        binding.sellerNameTxt.setText("The Witcher Cake Shop");

        binding.addToCartBtn.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
            String khachHangID = preferences.getString("khachHangID", null);

            if (khachHangID != null) {
                // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
                db.collection("KhachHang")
                        .document(khachHangID)
                        .collection("Cart")
                        .document(item.getID())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Sản phẩm đã có trong giỏ hàng, cập nhật số lượng
                                Long soLuongHienTai = documentSnapshot.getLong("soLuong");
                                int soLuongMoi = soLuongHienTai != null ? soLuongHienTai.intValue() + numberOrder : numberOrder;

                                Map<String, Object> gioHangData = new HashMap<>();
                                gioHangData.put("soLuong", soLuongMoi);

                                db.collection("KhachHang")
                                        .document(khachHangID)
                                        .collection("Cart")
                                        .document(item.getID())
                                        .update(gioHangData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(DetailActivity.this, "Cập nhật số lượng thành công!",
                                                    Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(DetailActivity.this, "Cập nhật số lượng thất bại!",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Sản phẩm chưa có trong giỏ hàng, thêm mới
                                item.setNumberInCart(numberOrder);

                                Map<String, Object> gioHangData = new HashMap<>();
                                gioHangData.put("sanPhamID", item.getID());
                                gioHangData.put("tenSanPham", item.getTitle());
                                gioHangData.put("giaTien", item.getPrice());
                                gioHangData.put("soLuong", item.getNumberInCart());
                                gioHangData.put("hinhAnh", item.getPicUrl().get(0));

                                db.collection("KhachHang")
                                        .document(khachHangID)
                                        .collection("Cart")
                                        .document(item.getID())
                                        .set(gioHangData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(DetailActivity.this, "Thêm vào giỏ hàng thành công!",
                                                    Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(DetailActivity.this, "Thêm vào giỏ hàng thất bại!",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DetailActivity.this, "Lỗi khi kiểm tra giỏ hàng: " +
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(DetailActivity.this, "Vui lòng đăng nhập để thêm vào giỏ hàng",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(DetailActivity.this, MainActivity.class)));

        binding.cartBtn.setOnClickListener(v -> {

                startActivity(new Intent(DetailActivity.this, CartActivity.class));
        });


        binding.msgToSellerBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + "0987654321"));
            sendIntent.putExtra("sms_body", "Nhập nội dung tin nhắn");
            startActivity(sendIntent);
        });

        binding.callToSellerBtn.setOnClickListener(v -> {
            String phone = String.valueOf("0987654321");
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        });
    }
}