package com.example.witchersshoes.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.witchersshoes.R;
import com.example.witchersshoes.Model.ProductCategory1;

import java.util.List;


public class Category1Adapter extends RecyclerView.Adapter<Category1Adapter.Category1ViewHolder> {

    private List<ProductCategory1> category1List;

    public Category1Adapter(List<ProductCategory1> category1List) {
        this.category1List = category1List;
    }

    @NonNull
    @Override
    public Category1ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category1_product_item, parent, false);
        return new Category1ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Category1ViewHolder holder, int position) {
        ProductCategory1 category1 = category1List.get(position);
        holder.imageViewProduct.setImageResource(category1.getImageResId());
        holder.textViewProductName.setText(category1.getName());
        holder.textViewProductPrice.setText("₫" + category1.getPrice());
    }

    @Override
    public int getItemCount() {
        return category1List.size();
    }

    public static class Category1ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewProductName, textViewProductPrice;
        Button buttonAddToCart;

        public Category1ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageView9);
            textViewProductName = itemView.findViewById(R.id.textView20);
            textViewProductPrice = itemView.findViewById(R.id.textView21);
            buttonAddToCart = itemView.findViewById(R.id.button5);
        }
    }
}
