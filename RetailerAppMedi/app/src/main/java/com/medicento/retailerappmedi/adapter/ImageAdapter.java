package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        view.getLayoutParams().height = (int)(metrics.heightPixels*0.70);
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

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
        }
    }
}
