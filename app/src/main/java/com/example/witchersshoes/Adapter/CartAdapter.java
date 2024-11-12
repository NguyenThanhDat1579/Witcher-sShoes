package com.example.witchersshoes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.witchersshoes.Helper.ChangeNumberItemsListener;
import com.example.witchersshoes.Helper.ManagmentCart;
import com.example.witchersshoes.Model.ProductModel;
import com.example.witchersshoes.databinding.ViewholderCartBinding;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<ProductModel> listItemSelected;
    private ManagmentCart managmentCart;
    private ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<ProductModel> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        this.changeNumberItemsListener = changeNumberItemsListener;
        this.managmentCart = new ManagmentCart(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel item = listItemSelected.get(position);
        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText(item.getPrice()+".000₫");
        holder.binding.totalEachItem.setText(Math.round(item.getNumberInCart()*item.getPrice())+".000₫");
        holder.binding.numberItemTxt.setText((String.valueOf(item.getNumberInCart())));

        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl().get(0))
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.picCart);


        holder.binding.plusCartBtn.setOnClickListener(v -> managmentCart.plusItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            if (changeNumberItemsListener!=null){
                changeNumberItemsListener.onChanged();
            }
        }));

        holder.binding.minusCartBtn.setOnClickListener(v -> managmentCart.minusItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            if (changeNumberItemsListener!=null) {
                changeNumberItemsListener.onChanged();
            }
        }));
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;
        public ViewHolder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
