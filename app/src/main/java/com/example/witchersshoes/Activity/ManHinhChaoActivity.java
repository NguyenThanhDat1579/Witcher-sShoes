package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.R;

public class ManHinhChaoActivity extends AppCompatActivity {
    TextView txtIntro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_hinh_chao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtIntro = findViewById(R.id.txtIntro);
        String text = "Thỏa Mãn Cơn Thèm Của Bạn Với Bánh Tươi, Donut, và Bánh Ngọt";
        // Tạo SpannableString để áp dụng màu
        SpannableString spannableString = new SpannableString(text);

        // Tạo một màu nâu
        int brownColor = Color.parseColor("#8B4513"); // Mã màu nâu

        // Đặt màu nâu cho các từ cụ thể
        spannableString.setSpan(new ForegroundColorSpan(brownColor), 30, 41, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Fresh Cakes
        spannableString.setSpan(new ForegroundColorSpan(brownColor), 41, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Donuts
        spannableString.setSpan(new ForegroundColorSpan(brownColor), 51, 60, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Pastries

        // Gán SpannableString cho TextView
        txtIntro.setText(spannableString);



        // đếm ngược trước khi chuyển activity
        CountDownTimer timer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(ManHinhChaoActivity.this, DangNhap.class));
                finish(); // một đi không trở lại
            }
        }.start();
    }
}