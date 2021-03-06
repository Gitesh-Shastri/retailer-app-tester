package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.medicento.retailerappmedi.EssentialsActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.Category;

import java.util.ArrayList;

public class EssentialAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<Category> categories;
    private Context context;
    private DisplayMetrics metrics;
    private boolean isNotShownBuy;

    public EssentialAdapter(ArrayList<Category> categories, Context context, DisplayMetrics metrics) {
        this.categories = categories;
        this.context = context;
        this.metrics = metrics;
        this.isNotShownBuy = false;
    }

    public EssentialAdapter(ArrayList<Category> categories, Context context, DisplayMetrics metrics, boolean isNotShownBuy) {
        this.categories = categories;
        this.context = context;
        this.metrics = metrics;
        this.isNotShownBuy = isNotShownBuy;
    }

    private setOnClickListener setOnClickListener;

    public interface setOnClickListener {
        public void onClickCategory(String id);
    }

    public void setSetOnClickListener(setOnClickListener setOnClickListener) {
        this.setOnClickListener = setOnClickListener;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_essential, viewGroup, false);

        view.getLayoutParams().width = (int)(metrics.widthPixels*0.28);
        view.requestLayout();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        Button buy;
        TextView name;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            buy = itemView.findViewById(R.id.buy);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }

        @Override
        protected void clear() {
            buy.setVisibility(View.VISIBLE);
            name.setText("");
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            if (isNotShownBuy) {
                buy.setVisibility(View.GONE);
            }

            name.setText(categories.get(position).getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isNotShownBuy) {
                        context.startActivity(new Intent(context, EssentialsActivity.class)
                                .putExtra("category", name.getText().toString()));
                    } else {
                        if (setOnClickListener != null) {
                            setOnClickListener.onClickCategory(categories.get(getCurrentPosition()).getId());
                        }
                    }
                }
            });

            Glide.with(context).load(categories.get(position).getImage_url()).into(image);
        }
    }
}
