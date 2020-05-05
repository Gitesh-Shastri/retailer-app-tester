package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.medicento.retailerappmedi.EssentialsActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.Category;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<Category> groups;
    private Context context;

    public GroupAdapter(ArrayList<Category> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_group, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    @Override
    public int getItemCount() {
        return groups != null ? groups.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        ImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            name.setText(groups.get(position).getName());
            Glide.with(context).load(groups.get(position).getImage_url()).into(image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, EssentialsActivity.class)
                            .putExtra("category", name.getText().toString()));
                }
            });
        }
    }
}
