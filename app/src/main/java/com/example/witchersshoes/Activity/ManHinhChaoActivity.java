package com.example.witchersshoes.Activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.R;

public class ManHinhChaoActivity extends AppCompatActivity {
    TextView txtIntro;
    LinearLayout mainLayout;
    ImageView myImageView1, myImageView2;
    ObjectAnimator moveRight1, moveRight2;


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
        mainLayout = findViewById(R.id.main);
        myImageView1 = findViewById(R.id.myImageView1);
        myImageView2 = findViewById(R.id.myImageView2);
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


        // Animation cho myImageView1 (di chuyển và nhảy)
        ObjectAnimator moveRight1 = ObjectAnimator.ofFloat(myImageView1, "translationX", 0f, 350f);
        moveRight1.setDuration(3000); // Thời gian di chuyển (3 giây)

        ObjectAnimator bounce1 = ObjectAnimator.ofFloat(myImageView1, "translationY", 0f, -90f, 0f);
        bounce1.setDuration(1000); // Thời gian nhảy lên xuống
        bounce1.setRepeatCount(ObjectAnimator.INFINITE); // Lặp lại animation nhảy

        AnimatorSet animatorSet1 = new AnimatorSet();
        animatorSet1.playTogether(moveRight1, bounce1);

        // Animation cho myImageView2 (chỉ di chuyển thẳng)
        ObjectAnimator moveRight2 = ObjectAnimator.ofFloat(myImageView2, "translationX", 0f, 350f);
        moveRight2.setDuration(3000); // Thời gian di chuyển (3 giây)
        moveRight2.setStartDelay(300); // Độ trễ 300ms so với myImageView1

        // Bắt đầu cả hai animation
        animatorSet1.start();
        moveRight2.start();
        // Sau 3 giây, chuyển sang màn hình đăng nhập
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(ManHinhChaoActivity.this, DangNhap.class);
            startActivity(intent);
            finish(); // Đóng Activity hiện tại
        }, 3000); // 3000ms = 3 giây

    }





}