package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.witchersshoes.Adapter.BestSellerAdapter;
import com.example.witchersshoes.Adapter.SeeMoreProductAdapter;
import com.example.witchersshoes.R;
import com.example.witchersshoes.ViewModel.MainViewModel;
import com.example.witchersshoes.databinding.ActivityMainBinding;
import com.example.witchersshoes.databinding.ActivitySeeMoreProductBinding;

public class SeeMoreProductActivity extends BaseActivity {

    private SeeMoreProductAdapter seeMoreProductAdapter;
    private ActivitySeeMoreProductBinding binding;
    private MainViewModel viewModel = new MainViewModel();
    ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeeMoreProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backBtn = findViewById(R.id.backBtn);


        initBestSeller();
        setVariable();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }

    private void initBestSeller() {
        binding.progressBarSeeMore.setVisibility(View.VISIBLE);
        viewModel.getBestSeller().observe(this, items -> {
            binding.seemoreproductRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            seeMoreProductAdapter = new SeeMoreProductAdapter(items, false);
            binding.seemoreproductRecyclerView.setAdapter(seeMoreProductAdapter);
            binding.progressBarSeeMore.setVisibility(View.GONE);
        });
        viewModel.loadBestSeller();
    }

    private void setVariable() {
        backBtn.setOnClickListener(v -> startActivity(new Intent(SeeMoreProductActivity.this, MainActivity.class)));

    }
}