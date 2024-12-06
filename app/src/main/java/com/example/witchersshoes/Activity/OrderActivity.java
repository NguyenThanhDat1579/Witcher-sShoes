package com.example.witchersshoes.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.witchersshoes.Model.NotificationModel;
import com.example.witchersshoes.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
                String message = "Đơn hàng sẽ được giao nhanh nhất có thể, bạn nhé";
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String time = sdf.format(date);

                // Lưu thông báo vào SharedPreferences
                saveNotification(title, message, time);

                // Tạo kênh thông báo (Chỉ cần tạo 1 lần, có thể đặt ở nơi khởi tạo ứng dụng)
                String channelId = "order_notification_channel";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            "Thông báo Đặt Hàng",
                            NotificationManager.IMPORTANCE_HIGH
                    );
                    channel.setDescription("Thông báo khi đặt hàng thành công");
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }
                }

                // Tạo Intent để mở NotificationActivity khi bấm vào thông báo
                Intent intent = new Intent(OrderActivity.this, NotificationActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        OrderActivity.this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // Tạo và hiển thị thông báo
                NotificationCompat.Builder builder = new NotificationCompat.Builder(OrderActivity.this, channelId)
                        .setSmallIcon(R.mipmap.ic_logo_foreground) // Thay icon thông báo
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(OrderActivity.this);
                notificationManagerCompat.notify((int) System.currentTimeMillis(), builder.build());


                // Điều hướng về MainActivity
                Intent mainIntent = new Intent(OrderActivity.this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

    }

    private void saveNotification(String title, String message, String time) {
        SharedPreferences sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Lấy danh sách thông báo cũ
        String existingNotifications = sharedPreferences.getString("notification_list", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<NotificationModel>>() {}.getType();
        List<NotificationModel> notificationList = gson.fromJson(existingNotifications, type);

        // Thêm thông báo mới vào danh sách
        notificationList.add(new NotificationModel(title, message, time));

        // Lưu lại danh sách thông báo
        String updatedNotifications = gson.toJson(notificationList);
        editor.putString("notification_list", updatedNotifications);
        editor.apply();
    }
}