package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.medicento.retailerappmedi.EssentialsActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.activity.ParticularOrderActivity;
import com.medicento.retailerappmedi.data.Category;
import com.medicento.retailerappmedi.data.Essential;

import java.util.ArrayList;

public class EssentialAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<Category> categories;
    private Context context;
    private DisplayMetrics metrics;

    public EssentialAdapter(ArrayList<Category> categories, Context context, DisplayMetrics metrics) {
        this.categories = categories;
        this.context = context;
        this.metrics = metrics;
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

        public ViewHolder(View itemView) {
            super(itemView);

            buy = itemView.findViewById(R.id.buy);
            name = itemView.findViewById(R.id.name);
        }

        @Override
        protected void clear() {
            name.setText("");
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            name.setText(categories.get(position).getName());

            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, EssentialsActivity.class)
                    .putExtra("category", name.getText().toString()));
                }
            });
        }
    }
}
