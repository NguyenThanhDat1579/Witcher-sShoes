package com.example.witchersshoes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.witchersshoes.Activity.DetailActivity;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.databinding.ViewholderBestSellerBinding;

import java.util.List;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.ViewHolder> {

    private List<ProductModel> items;
    private Context context;

    public BestSellerAdapter(List<ProductModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ViewholderBestSellerBinding binding;
        public ViewHolder(ViewholderBestSellerBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
