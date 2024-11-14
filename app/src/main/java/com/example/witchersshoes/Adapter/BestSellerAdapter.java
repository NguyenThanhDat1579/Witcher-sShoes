// BestSellerAdapter.java
package com.example.witchersshoes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.witchersshoes.Activity.DetailActivity;
import com.example.witchersshoes.Model.FavoriteEvent;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.databinding.ViewholderBestSellerBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.ViewHolder> {
    private List<ProductModel> items;
    private Context context;
    private FirebaseFirestore db;
    private String khachHangID;
    private boolean isFavoriteList;

    public BestSellerAdapter(List<ProductModel> items) {
        this.items = items;
        this.isFavoriteList = false;
        this.db = FirebaseFirestore.getInstance();
    }

    // Constructor cho màn hình Favorite
    public BestSellerAdapter(List<ProductModel> items, boolean isFavoriteList) {
        this.items = items;
        this.isFavoriteList = isFavoriteList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Lấy khachHangID từ SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("THONGTIN", Context.MODE_PRIVATE);
        khachHangID = preferences.getString("khachHangID", null);

        ViewholderBestSellerBinding binding = ViewholderBestSellerBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel item = items.get(position);
        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText(item.getPrice()+"00₫");
        holder.binding.ratingTxt.setText(String.valueOf(item.getRating()));
        holder.binding.txtID.setText(String.valueOf(item.getID()));

        Glide.with(context)
                .load(item.getPicUrl().get(0))
                .apply(new RequestOptions().transform(new CenterCrop()))
                .into(holder.binding.picBestSeller);

        // Kiểm tra trạng thái yêu thích và cập nhật UI
        checkFavoriteStatus(item.getID(), holder);

        holder.binding.favoriteBtn.setOnClickListener(v -> {
            if (khachHangID == null) {
                Toast.makeText(context, "Vui lòng đăng nhập để thêm vào yêu thích",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            toggleFavorite(item, holder);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);
        });
    }

    private void checkFavoriteStatus(String productId, ViewHolder holder) {
        if (khachHangID != null) {
            db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Favorite")
                    .document(productId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            holder.binding.favoriteBtn.setSelected(true);
                        } else {
                            holder.binding.favoriteBtn.setSelected(false);
                        }
                    });
        }
    }

    private void toggleFavorite(ProductModel item, ViewHolder holder) {
        String productId = item.getID();

        if (holder.binding.favoriteBtn.isSelected()) {
            // Remove from favorites
            db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Favorite")
                    .document(productId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        holder.binding.favoriteBtn.setSelected(false);
                        Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích",
                                Toast.LENGTH_SHORT).show();

                        // Post event khi xóa khỏi yêu thích
                        EventBus.getDefault().post(new FavoriteEvent(productId, false));

                        if (isFavoriteList) {
                            int pos = items.indexOf(item);
                            items.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context,
                            "Lỗi khi xóa khỏi yêu thích: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        } else {
            // Add to favorites
            Map<String, Object> favoriteItem = new HashMap<>();
            favoriteItem.put("title", item.getTitle());
            favoriteItem.put("price", item.getPrice());
            favoriteItem.put("rating", item.getRating());
            favoriteItem.put("picUrl", item.getPicUrl());
            favoriteItem.put("description", item.getDescription());

            db.collection("KhachHang")
                    .document(khachHangID)
                    .collection("Favorite")
                    .document(productId)
                    .set(favoriteItem)
                    .addOnSuccessListener(aVoid -> {
                        holder.binding.favoriteBtn.setSelected(true);
                        Toast.makeText(context, "Đã thêm vào danh sách yêu thích",
                                Toast.LENGTH_SHORT).show();

                        // Post event khi thêm vào yêu thích
                        EventBus.getDefault().post(new FavoriteEvent(productId, true));
                    })
                    .addOnFailureListener(e -> Toast.makeText(context,
                            "Lỗi khi thêm vào yêu thích: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        }
    }

    // Thêm method để update trạng thái yêu thích
    public void updateFavoriteStatus(String productId, boolean isFavorite) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getID().equals(productId)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderBestSellerBinding binding;

        public ViewHolder(ViewholderBestSellerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}