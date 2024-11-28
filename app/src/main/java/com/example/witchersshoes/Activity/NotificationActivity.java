package com.example.witchersshoes.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Adapter.NotificationAdapter;
import com.example.witchersshoes.Model.NotificationModel;
import com.example.witchersshoes.R;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerViewNotification);

        getData();
        // Lấy dữ liệu từ Bundle
    }
    private void getData(){
        ArrayList<NotificationModel> notifications = new ArrayList<>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String title = bundle.getString("title");
            String message = bundle.getString("message");
            String formattedTime = bundle.getString("time");
            NotificationModel notification = new NotificationModel(title, message, formattedTime);
            notifications.add(notification);
        }

        NotificationAdapter adapter = new NotificationAdapter(notifications, NotificationActivity.this);
        recyclerView.setAdapter(adapter);
    }
}