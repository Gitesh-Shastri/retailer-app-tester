package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.medicento.retailerappmedi.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<String> images;
    private Context context;
    private DisplayMetrics metrics;

    public ImageAdapter(ArrayList<String> images, Context context, DisplayMetrics metrics) {
        this.images = images;
        this.context = context;
        this.metrics = metrics;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_image_slider, viewGroup, false);

        view.getLayoutParams().height = (int)(metrics.heightPixels*0.60);
        view.requestLayout();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        ImageView mask_image;

        public ViewHolder(View itemView) {
            super(itemView);

            mask_image = itemView.findViewById(R.id.mask_image);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            Glide.with(context).load(images.get(position)).into(mask_image);
        }
    }
}
