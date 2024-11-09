package com.example.witchersshoes.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.witchersshoes.R;
import com.example.witchersshoes.Model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public TextView productName;
        public TextView productPrice;
        public Button addToCartButton;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(product.getPrice());

        String imageUrl = product.getImage();
        Log.d("ProductAdapter", "Image URL: " + imageUrl);
        Glide.with(holder.itemView.getContext())
                .load(product.getImage())  // URL của ảnh từ Product
                .placeholder(R.drawable.ic_launcher_foreground)  // Ảnh hiển thị khi chờ tải
                .error(R.drawable.ic_launcher_foreground)  // Ảnh hiển thị khi có lỗi
                .into(holder.productImage);

        holder.addToCartButton.setOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn nút "Thêm vào giỏ hàng"
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
