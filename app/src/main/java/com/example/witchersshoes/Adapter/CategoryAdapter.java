package com.example.witchersshoes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.witchersshoes.Activity.CategoryActivity;
import com.example.witchersshoes.Model.CategoryModel;
import com.example.witchersshoes.databinding.ViewholderCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> items;
    private Context context;

    public CategoryAdapter(List<CategoryModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel item = items.get(position);
        holder.binding.titleCat.setText(item.getTitle());
        holder.binding.txtIDCategory.setText(item.getId());

        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl())
                .into(holder.binding.picCat);

        holder.binding.btnCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = holder.itemView.getContext();
                String id = item.getId();
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("title", item.getTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCategoryBinding binding;
        public ViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
