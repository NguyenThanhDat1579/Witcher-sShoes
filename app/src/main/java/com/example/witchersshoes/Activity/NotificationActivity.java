package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.NotificationAdapter;
import com.example.witchersshoes.Model.NotificationModel;
import com.example.witchersshoes.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        RecyclerView rvNotifications = findViewById(R.id.recyclerViewNotification);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE);
        String existingNotifications = sharedPreferences.getString("notification_list", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<NotificationModel>>() {}.getType();
        List<NotificationModel> notificationList = gson.fromJson(existingNotifications, type);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        ArrayList<String> productNames = intent.getStringArrayListExtra("productNames");
        ArrayList<Integer> productQuantities = intent.getIntegerArrayListExtra("productQuantities");
        double totalAmount = intent.getDoubleExtra("totalAmount", 0);

        NotificationAdapter adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);
    }



}