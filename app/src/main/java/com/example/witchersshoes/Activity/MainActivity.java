package com.example.witchersshoes.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.witchersshoes.Adapter.BestSellerAdapter;
import com.example.witchersshoes.Adapter.CategoryAdapter;
import com.example.witchersshoes.Adapter.SeeMoreProductAdapter;
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

import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends BaseActivity {
    private BestSellerAdapter bestSellerAdapter;
    private ActivityMainBinding binding;
    private MainViewModel viewModel = new MainViewModel();
    private boolean isBackPressedOnce = false;
    TextView txtName;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;


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

        binding.seemoreproductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SeeMoreProductActivity.class));
            }
        });

        binding.seemorecategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SeeMoreCategoryActivity.class));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký EventBus
        EventBus.getDefault().unregister(this);
        sliderHandler.removeCallbacks(sliderRunnable); // Dừng auto-slide khi Activity bị hủy
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

    public class SmoothPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();

            if (position < -1) { // Trang ngoài bên trái
                page.setAlpha(0f);
            } else if (position <= 1) { // Trang trong khoảng [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
                float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;

                if (position < 0) {
                    page.setTranslationX(horizontalMargin - verticalMargin / 2);
                } else {
                    page.setTranslationX(-horizontalMargin + verticalMargin / 2);
                }

                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else { // Trang ngoài bên phải
                page.setAlpha(0f);
            }
        }
    }

    public class ViewPager2Scroller extends RecyclerView {
        public ViewPager2Scroller(Context context, RecyclerView recyclerView) {
            super(context);
            try {
                Field layoutManagerField = ViewPager2.class.getDeclaredField("mLayoutManager");
                layoutManagerField.setAccessible(true);

                RecyclerView.LayoutManager layoutManager = (RecyclerView.LayoutManager) layoutManagerField.get(recyclerView);
                Field scrollerField = layoutManager.getClass().getDeclaredField("mRecyclerView");
                scrollerField.setAccessible(true);

                Scroller scroller = new Scroller(context, new DecelerateInterpolator());
                scrollerField.set(layoutManager, scroller);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupBanners(List<SliderModel> images) {
        binding.viewPager2.setAdapter(new SliderAdapter(images, binding.viewPager2));
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPager2.setPageTransformer(new SmoothPageTransformer());

        if (images.size() > 1) {
            binding.dotIndicator.setVisibility(RecyclerView.VISIBLE);
            binding.dotIndicator.attachTo(binding.viewPager2);
        }

        // Tự động chuyển banner
        sliderRunnable = () -> {
            int currentItem = binding.viewPager2.getCurrentItem();
            int nextItem = (currentItem + 1) % images.size(); // Quay lại trang đầu nếu hết trang
            binding.viewPager2.setCurrentItem(nextItem, true); // Bật smoothScroll
            sliderHandler.postDelayed(sliderRunnable, 5000); // Lặp lại sau 5 giây
        };

        sliderHandler.postDelayed(sliderRunnable, 5000); // Bắt đầu sau 5 giây

        // Lắng nghe sự kiện cuộn tay để khởi động lại Handler
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable); // Dừng khi người dùng cuộn
                sliderHandler.postDelayed(sliderRunnable, 5000); // Tiếp tục sau 5 giây
            }
        });


        ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 5000);
            }
        };
        binding.viewPager2.registerOnPageChangeCallback(callback);

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