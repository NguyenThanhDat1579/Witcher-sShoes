package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.witchersshoes.Adapter.BestSellerAdapter;
import com.example.witchersshoes.Adapter.CategoryAdapter;
import com.example.witchersshoes.Adapter.SliderAdapter;
import com.example.witchersshoes.Model.Customer;
import com.example.witchersshoes.Model.FavoriteEvent;
import com.example.witchersshoes.Model.SliderModel;
import com.example.witchersshoes.R;
import com.example.witchersshoes.ViewModel.MainViewModel;
import com.example.witchersshoes.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainActivity extends BaseActivity {
    private BestSellerAdapter bestSellerAdapter;
    private ActivityMainBinding binding;
    private MainViewModel viewModel = new MainViewModel();
    private boolean isBackPressedOnce = false;
    TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Đăng ký EventBus
        EventBus.getDefault().register(this);

        txtName = findViewById(R.id.txtName);
        // Lấy dữ liệu tenKhachHang từ Intent
        SharedPreferences preferences = getSharedPreferences("THONGTIN", MODE_PRIVATE);
        String khachHangID = preferences.getString("khachHangID", null);

        getInfoUser(khachHangID);

        initBanners();
        initCategory();
        initBestSeller();
        bottomNavigation();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký EventBus
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (isBackPressedOnce) {
            // Nếu nhấn lại lần nữa, thoát ứng dụng
            finishAffinity();
        } else {
            // Hiển thị thông báo
            isBackPressedOnce = true;
            Toast.makeText(this, "Nhấn lần nữa để thoát ứng dụng", Toast.LENGTH_SHORT).show();

            // Đặt lại trạng thái sau 2 giây
            new Handler().postDelayed(() -> isBackPressedOnce = false, 2000);
        }
    }

    private void bottomNavigation() {
        binding.exploreBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        });
        binding.cartBtn.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, CartActivity.class));

        });
        binding.favoriteBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FavoriteActivity.class));

        });

        binding.orderBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrderDetailActivity.class));

        });

        binding.profileBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));

        });
    }

    private void initBestSeller() {
        binding.progressBarBestSeller.setVisibility(View.VISIBLE);
        viewModel.getBestSeller().observe(this, items -> {
            binding.recyclerViewBestSeller.setLayoutManager(new GridLayoutManager(this, 2));
            bestSellerAdapter = new BestSellerAdapter(items, false);
            binding.recyclerViewBestSeller.setAdapter(bestSellerAdapter);
            binding.progressBarBestSeller.setVisibility(View.GONE);
        });
        viewModel.loadBestSeller();
    }

    // Method để nhận event từ EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFavoriteEvent(FavoriteEvent event) {
        if (bestSellerAdapter != null) {
            bestSellerAdapter.updateFavoriteStatus(event.getProductId(), event.isFavorite());
        }
    }

    private void initCategory() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        viewModel.getCategory().observe(this, items -> {
            binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerViewCategory.setAdapter(new CategoryAdapter(items));
            binding.progressBarCategory.setVisibility(View.GONE);
        });
        viewModel.loadCategory();
    }

    private void initBanners() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getSlider().observe(this, banners -> {
            setupBanners(banners);
            binding.progressBar.setVisibility(View.GONE);
        });
        viewModel.loadSlider();
    }

    private void setupBanners(List<SliderModel> images) {
        binding.viewPager2.setAdapter(new SliderAdapter(images, binding.viewPager2));
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPager2.setPageTransformer(transformer);

        if (images.size() > 1) {
            binding.dotIndicator.setVisibility(RecyclerView.VISIBLE);
            binding.dotIndicator.attachTo(binding.viewPager2);
        }

    }

    private void getInfoUser(String KhachHangID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("KhachHang")
                .document(KhachHangID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        Customer customer = new Customer();
                        customer.setId(documentSnapshot.getId());
                        customer.setUsername(documentSnapshot.getString("tenKhachHang"));
                        customer.setEmail(documentSnapshot.getString("email"));


                        txtName.setText(customer.getUsername());

                    }
                });
    }
}