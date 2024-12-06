package com.example.witchersshoes.Activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.SearchProductAdapter;
import com.example.witchersshoes.R;
import com.example.witchersshoes.ViewModel.MainViewModel;
import com.example.witchersshoes.databinding.ActivitySreachProductBinding;


public class SearchProductActivity extends AppCompatActivity {

    private SearchProductAdapter SearchProductAdapter;
    private ActivitySreachProductBinding binding;
    private MainViewModel viewModel = new MainViewModel();
    private ImageView backBtn;
    private RecyclerView seemoreproduct_recyclerView1;
    private ProgressBar progressBarSeeMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySreachProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        seemoreproduct_recyclerView1 = findViewById(R.id.seemoreproduct_recyclerView1);
        progressBarSeeMore = findViewById(R.id.progressBarSeeMore);
        backBtn = findViewById(R.id.backBtn);

        String query = getIntent().getStringExtra("query");
        binding.txtText.setText(query);

        // Khởi tạo danh sách sản phẩm
        initBestSeller();

        // Thiết lập giao diện hệ thống cho status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);

        // Xử lý sự kiện quay lại trang chính
        setVariable();
    }

    private void initBestSeller() {
        binding.progressBarSeeMore.setVisibility(View.VISIBLE);
        viewModel.getBestSeller().observe(this, items -> {
            seemoreproduct_recyclerView1.setLayoutManager(new GridLayoutManager(this, 2));
            SearchProductAdapter = new SearchProductAdapter(items, false);
            seemoreproduct_recyclerView1.setAdapter(SearchProductAdapter);
            progressBarSeeMore.setVisibility(View.GONE);

            // Lọc danh sách sản phẩm dựa trên query
            String query = getIntent().getStringExtra("query");
            boolean hasResults = SearchProductAdapter.filterList(query);

            // Kiểm tra xem có sản phẩm phù hợp không
            if (!hasResults) {
                binding.txtText.setText("Không tìm thấy sản phẩm phù hợp");
                binding.txtText.setVisibility(View.VISIBLE);  // Hiển thị thông báo không tìm thấy
                binding.txtText.setGravity(Gravity.CENTER);  // Căn giữa thông báo
            } else {
                binding.txtText.setVisibility(View.GONE);  // Ẩn thông báo nếu có sản phẩm
            }
        });
        viewModel.loadBestSeller();
    }

    private void setVariable() {
        // Quay lại màn hình chính
        backBtn.setOnClickListener(v -> finish());
    }
}
