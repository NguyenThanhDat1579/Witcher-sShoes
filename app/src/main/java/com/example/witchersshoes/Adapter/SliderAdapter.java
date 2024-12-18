package com.example.witchersshoes.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.example.witchersshoes.Model.SliderModel;
import com.example.witchersshoes.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderModel> sliderModels;
    private ViewPager2 viewPager2;
    private Context context;

    public SliderAdapter(List<SliderModel> sliderModels, ViewPager2 viewPager2) {
        this.sliderModels = sliderModels;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.slider_image_container, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderModels.get(position), context);
        if (position == sliderModels.size() - 1){
            viewPager2.post(() -> viewPager2.setCurrentItem(0, false)); // Lặp lại từ đầu mà không gọi notifyDataSetChanged
        }
    }

    @Override
    public int getItemCount() {
        return sliderModels.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        public void setImage(SliderModel sliderModel, Context context) {
            imageView.setAlpha(0f); // Bắt đầu với alpha 0
            Glide.with(context)
                    .load(sliderModel.getUrl())
                    .apply(new RequestOptions().transform(new CenterInside()))
                    .into(imageView);

            imageView.animate().alpha(1f).setDuration(1000).start(); // Animation fade-in
        }
    }
}
