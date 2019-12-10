package com.medicento.retailerappmedi.data;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderAdapterDelivered extends RecyclerView.Adapter<OrderAdapterDelivered.RecentOrderCViewHolder>{

    public static ArrayList<RecentOrderDelivered> mOrderList;

    public OrderAdapterDelivered(ArrayList<RecentOrderDelivered> list) {
        mOrderList = list;
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick1(int position);
        void onCancelItem(int position);
        void onReturnItem(int position);
    }

    public void setOnItemClicklistener(OnItemClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public RecentOrderCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recent_orderc, parent, false);

        OrderAdapterDelivered.RecentOrderCViewHolder viewHolder = new OrderAdapterDelivered.RecentOrderCViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecentOrderCViewHolder holder, int position) {
        holder.bind(position);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public void add(RecentOrderDelivered recentOrder){
        mOrderList.add(recentOrder);
        notifyItemInserted(0);
    }

    public  class RecentOrderCViewHolder extends RecyclerView.ViewHolder{

        TextView orderId, orderDate, orderStatus,orderCost;

        Button cancel_order, return_order, view_details;

        public RecentOrderCViewHolder(View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orders_id_tv);
            orderDate = itemView.findViewById(R.id.order_date_tv);
            orderCost = itemView.findViewById(R.id.order_cost_tv);
            orderStatus = itemView.findViewById(R.id.status);
            cancel_order = itemView.findViewById(R.id.cancel_order);
            return_order = itemView.findViewById(R.id.return_order);
            view_details = itemView.findViewById(R.id.view_details);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        int position = getAdapterPosition();
                        if(position !=  RecyclerView.NO_POSITION) {
                            mListener.onItemClick1(position);
                        }
                    }
                }
            });

            view_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null) {
                        int position = getAdapterPosition();
                        if(position !=  RecyclerView.NO_POSITION) {
                            mListener.onItemClick1(position);
                        }
                    }
                }
            });

            cancel_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null) {
                        int position = getAdapterPosition();
                        if(position !=  RecyclerView.NO_POSITION) {
                            mListener.onCancelItem(position);
                        }
                    }
                }
            });

            return_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null) {
                        int position = getAdapterPosition();
                        if(position !=  RecyclerView.NO_POSITION) {
                            mListener.onReturnItem(position);
                        }
                    }
                }
            });
        }
        public void bind(int pos) {
            RecentOrderDelivered recentOrder = mOrderList.get(pos);
            orderId.setText(recentOrder.getpOrderId());
            orderDate.setText(recentOrder.getpDate());
            try {
                String price = "Rs. "+NumberFormat.getNumberInstance(Locale.US).format(Integer.valueOf(recentOrder.getTotal()));
                orderCost.setText(price);
            } catch (Exception e) {
                orderCost.setText("Rs. "+recentOrder.getTotal());
                e.printStackTrace();
            }

            orderStatus.setText(recentOrder.getStatus());
        }
    }
}
