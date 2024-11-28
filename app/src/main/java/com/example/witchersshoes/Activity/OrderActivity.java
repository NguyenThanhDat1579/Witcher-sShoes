package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderActivity extends AppCompatActivity {

    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                String title = "Đặt Hàng Thành Công";
                String message = "Đơn hàng sẽ được giao nhanh nhất có thế, bạn nhé";
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String time = sdf.format(date);

                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("message", message);
                bundle.putString("time", time);

                Intent i = new Intent(OrderActivity.this, MainActivity.class);
                Intent intent = new Intent(OrderActivity.this, NotificationActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtras(bundle);
                startActivity(i);
                finish();
            }
        });
    }
}