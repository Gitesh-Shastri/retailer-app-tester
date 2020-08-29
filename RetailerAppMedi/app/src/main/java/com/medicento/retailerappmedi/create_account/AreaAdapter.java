package com.medicento.retailerappmedi.create_account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.adapter.BaseViewHolder;
import com.medicento.retailerappmedi.adapter.DidntFindAdapter;
import com.medicento.retailerappmedi.adapter.EssentialAdapter;
import com.medicento.retailerappmedi.data.Area;

import java.util.ArrayList;

public class AreaAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<Area> areas;
    private Context context;

    public AreaAdapter(ArrayList<Area> areas, Context context) {
        this.areas = areas;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.area_name, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private setOnClickListener setOnClickListener;

    public interface setOnClickListener {
        public void onAreaClick(int positon);
    }

    public void setOnClickListener(setOnClickListener setOnClickListener) {
        this.setOnClickListener = setOnClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return areas != null ? areas.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            name.setText(areas.get(position).getName() + "\n" + areas.get(position).getCity() + " - " + areas.get(position).getState());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (setOnClickListener != null) {
                        setOnClickListener.onAreaClick(position);
                    }
                }
            });
        }
    }
}
