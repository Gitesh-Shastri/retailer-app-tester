package com.medicento.retailerappmedi.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.order_related.OrderItem;

import java.util.ArrayList;

public class OrderBounceItemsAdapter extends RecyclerView.Adapter<OrderBounceItemsAdapter.ViewHolder> {

    public static ArrayList<OrderItem> mRecentOrderItems;

    public OrderBounceItemsAdapter(ArrayList<OrderItem> recentOrderItems) {
        mRecentOrderItems = recentOrderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_ordered_medicine,viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.name.setText(mRecentOrderItems.get(i).getName());
        viewHolder.price.append("Rs. " +mRecentOrderItems.get(i).getPrice()+"");
        viewHolder.qty.append("Qty. " + mRecentOrderItems.get(i).getQty());

    }

    @Override
    public int getItemCount() {
        return mRecentOrderItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, qty, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.medicine_name);
            qty = itemView.findViewById(R.id.medicine_quantity);
            price = itemView.findViewById(R.id.medicine_cost);
        }
    }
}
