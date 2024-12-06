package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.witchersshoes.Adapter.CategoryAdapter;
import com.example.witchersshoes.Adapter.SeeMoreCategoryAdapter;
import com.example.witchersshoes.Adapter.SeeMoreProductAdapter;
import com.example.witchersshoes.Model.CategoryModel;
import com.example.witchersshoes.R;
import com.example.witchersshoes.ViewModel.MainViewModel;
import com.example.witchersshoes.databinding.ActivitySeeMoreCategoryBinding;
import com.example.witchersshoes.databinding.ActivitySeeMoreProductBinding;

import java.util.List;

public class SeeMoreCategoryActivity extends BaseActivity {

    private SeeMoreCategoryAdapter seeMoreCategoryAdapter;
    private ActivitySeeMoreCategoryBinding binding;
    private MainViewModel viewModel = new MainViewModel();
    ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeeMoreCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backBtn = findViewById(R.id.backBtn);


        initCategory();
        setVariable();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }

    private void initCategory() {
        binding.progressBarSeeMore.setVisibility(View.VISIBLE);
        viewModel.getCategory().observe(this, items -> {
            binding.seemorecategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false));
            binding.seemorecategoryRecyclerView.setAdapter(new SeeMoreCategoryAdapter(items));
            binding.progressBarSeeMore.setVisibility(View.GONE);
        });
        viewModel.loadCategory();
    }


    private void setVariable() {
        backBtn.setOnClickListener(v -> startActivity(new Intent(SeeMoreCategoryActivity.this, MainActivity.class)));

    }
}