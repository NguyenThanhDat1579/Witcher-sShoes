package com.example.witchersshoes.Activity;

import android.content.Context;
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
import com.example.witchersshoes.Adapter.BestSellerAdapter;
import com.example.witchersshoes.Adapter.PicListAdapter;
import com.example.witchersshoes.Model.FavoriteEvent;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.R;
import com.example.witchersshoes.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;

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
    private String khachHangID;
    private boolean isFavoriteList;
    private List<ProductModel> items;


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

        SharedPreferences preferences = this.getSharedPreferences("THONGTIN", Context.MODE_PRIVATE);
        khachHangID = preferences.getString("khachHangID", null);




    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFavoriteStatus(item.getID());
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


        if (item == null || item.getID() == null) {
            Toast.makeText(this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.titleTxt.setText(item.getTitle());
        binding.desciptionTxt.setText(item.getDescription());
        binding.priceTxt.setText(item.getPrice()+"00₫");
        binding.ratingTxt.setText(item.getRating() + " ");
        binding.sellerNameTxt.setText("The Witcher Cake Shop");

        // Kiểm tra trạng thái yêu thích
        checkFavoriteStatus(item.getID());

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

        binding.favBtn.setOnClickListener(v -> {
            if (khachHangID == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm vào yêu thích",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            toggleFavorite(item);
        });

    }

    private void toggleFavorite(ProductModel item) {
        String productId = item.getID();

        if (binding.favBtn.isSelected()) {
            // Xóa khỏi danh sách yêu thích
            db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Favorite")
                    .document(productId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Đảm bảo cập nhật trạng thái ngay tại đây
                        binding.favBtn.setSelected(false);
                        // Optional: cập nhật hình ảnh nếu cần
                         binding.favBtn.setImageResource(R.drawable.btn_3);
                        Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi xóa khỏi danh sách yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Thêm vào danh sách yêu thích
            Map<String, Object> favoriteItem = new HashMap<>();
            favoriteItem.put("title", item.getTitle());
            favoriteItem.put("price", item.getPrice());
            favoriteItem.put("rating", item.getRating());
            favoriteItem.put("picUrl", item.getPicUrl());
            favoriteItem.put("description", item.getDescription());

            db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Favorite")
                    .document(productId)
                    .set(favoriteItem)
                    .addOnSuccessListener(aVoid -> {
                        // Đảm bảo cập nhật trạng thái ngay tại đây
                        binding.favBtn.setSelected(true);
                        // Optional: cập nhật hình ảnh nếu cần
                         binding.favBtn.setImageResource(R.drawable.redheart);
                        Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi thêm vào danh sách yêu thích: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void checkFavoriteStatus(String productId) {
        if (khachHangID == null || productId == null) {
            binding.favBtn.setSelected(false);
            binding.favBtn.setImageResource(R.drawable.btn_3);
            return;
        }

        db.collection("KhachHang")
                .document(khachHangID)
                .collection("Favorite")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    boolean isFavorite = documentSnapshot.exists();
                    binding.favBtn.setSelected(isFavorite);

                    // Cập nhật icon dựa trên trạng thái
                    if (isFavorite) {
                        binding.favBtn.setImageResource(R.drawable.redheart);
                    } else {
                        binding.favBtn.setImageResource(R.drawable.btn_3);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CheckFavoriteStatus", "Lỗi kiểm tra trạng thái yêu thích", e);
                    binding.favBtn.setSelected(false);
                    binding.favBtn.setImageResource(R.drawable.btn_3);
                });
    }

}