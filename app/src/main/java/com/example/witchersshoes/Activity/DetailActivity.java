package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.witchersshoes.Adapter.PicListAdapter;
import com.example.witchersshoes.Helper.ManagmentCart;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.databinding.ActivityDetailBinding;

import java.util.ArrayList;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private ProductModel item;
    private int numberOrder = 1;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        managmentCart = new ManagmentCart(this);

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
        binding.priceTxt.setText(item.getPrice()+".000₫");
        binding.ratingTxt.setText(item.getRating() + " ");
        binding.sellerNameTxt.setText("The Witcher Cake Shop");

        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setNumberInCart(numberOrder);
                managmentCart.insertItems(item);
            }
        });

        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(DetailActivity.this, MainActivity.class)));

        binding.cartBtn.setOnClickListener(v -> {
            if (!managmentCart.getListCart().isEmpty()) {
                startActivity(new Intent(DetailActivity.this, CartActivity.class));
            } else {
                Toast.makeText(DetailActivity.this, "Giỏ hàng của bạn trống!", Toast.LENGTH_SHORT).show();
            }
        });


        binding.msgToSellerBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + "0981236547"));
            sendIntent.putExtra("sms_body", "Nhập nội dung tin nhắn");
            startActivity(sendIntent);
        });

        binding.callToSellerBtn.setOnClickListener(v -> {
            String phone = String.valueOf("0981236547");
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        });
    }
}