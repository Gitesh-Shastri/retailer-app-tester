package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.activity.UploadPurchaseActivity;

import java.util.ArrayList;

public class ImageFullScreenAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<String> urls;
    private Context context;

    public ImageFullScreenAdapter(ArrayList<String> urls, Context context) {
        this.urls = urls;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image_full_url, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return urls != null ? urls.size() : 0;
    }

    public class Viewholder extends BaseViewHolder {

        ImageView image;

        public Viewholder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }


        @Override
        protected void clear() {
            image.setImageDrawable(null);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            Glide.with(context).load(urls.get(position)).into(image);
        }
    }
}
