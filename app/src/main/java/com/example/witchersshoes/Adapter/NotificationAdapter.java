package com.example.witchersshoes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.Model.NotificationModel;
import com.example.witchersshoes.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationModel> items;
    private Context context;

    public NotificationAdapter(List<NotificationModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription, txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTiTleNotifi);
            txtDescription = itemView.findViewById(R.id.txtDescriptNotifi);
            txtTime = itemView.findViewById(R.id.txtTimeNotifi);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho từng item trong RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = items.get(position);

        // Hiển thị dữ liệu title, description
        holder.txtTitle.setText(item.getTitle());
        holder.txtDescription.setText(item.getMessage());

        // Hiển thị thời gian (đã được định dạng từ NotificationActivity)
        holder.txtTime.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

