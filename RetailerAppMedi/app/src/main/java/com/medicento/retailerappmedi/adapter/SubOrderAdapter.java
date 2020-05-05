package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.order_related.SubOrder;

import java.util.ArrayList;

public class SubOrderAdapter extends RecyclerView.Adapter<SubOrderAdapter.ViewHolder> {

    private ArrayList<SubOrder> mRecentOrderItems;
    private Context context;
    private boolean isOnReturnScreen;

    public boolean isOnReturnScreen() {
        return isOnReturnScreen;
    }

    public void setOnReturnScreen(boolean onReturnScreen) {
        isOnReturnScreen = onReturnScreen;
    }

    public SubOrderAdapter(ArrayList<SubOrder> mRecentOrderItems, Context context) {
        this.mRecentOrderItems = mRecentOrderItems;
        this.context = context;
        this.isOnReturnScreen = false;
    }

    public ArrayList<SubOrder> getmRecentOrderItems() {
        return mRecentOrderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_sub_order,viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final SubOrder subOrder = mRecentOrderItems.get(i);
        viewHolder.sub_order_id.setText(subOrder.getId());
        viewHolder.status.setText(subOrder.getStatus());
        viewHolder.sub_supplier.setText(subOrder.getSuplier_name());
        viewHolder.sub_total.setText("Rs. "+subOrder.getTotal());

        viewHolder.orderItemAdapter = new OrderItemAdapter(subOrder.getOrderItems(), context);

        viewHolder.sub_order_items_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        viewHolder.sub_order_items_rv.setAdapter(viewHolder.orderItemAdapter);

        viewHolder.orderItemAdapter.setOnReturnScreen(isOnReturnScreen);

        viewHolder.orderItemAdapter.setOnItemClicklistener(new OrderItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick1(int position, boolean b) {
                try {
                    mRecentOrderItems.get(i).getOrderItems().get(position).setSelected(b);
                    mRecentOrderItems.get(i).getOrderItems().get(position).setReturned(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecentOrderItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sub_order_id, status, sub_supplier, sub_total;
        RecyclerView sub_order_items_rv;

        OrderItemAdapter orderItemAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sub_order_id = itemView.findViewById(R.id.sub_order_id);
            status = itemView.findViewById(R.id.status);
            sub_supplier = itemView.findViewById(R.id.sub_supplier);
            sub_total = itemView.findViewById(R.id.sub_total);
            sub_order_items_rv = itemView.findViewById(R.id.sub_order_items_rv);
        }
    }
}

