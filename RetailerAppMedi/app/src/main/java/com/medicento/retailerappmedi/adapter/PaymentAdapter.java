package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.Notification;
import com.medicento.retailerappmedi.data.order_related.Payment;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder>{

    private ArrayList<Payment> payments;
    private Context context;
    private Notification notification;
    private int discount = 0;

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public PaymentAdapter(ArrayList<Payment> payments, Context context) {
        this.payments = payments;
        this.context = context;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i==0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_date, viewGroup, false);
            return new DateViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_payment_summary, viewGroup, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Payment item = payments.get(i);
        if(item.isDate()) {
            DateViewHolder dateViewHolder = (DateViewHolder) viewHolder;
            dateViewHolder.date.setText(item.getDate());
        } else {
            final ViewHolder viewHolder1 = (ViewHolder) viewHolder;
            if (notification != null && notification.getCreated_at().equals(item.getDate()) && notification.getTitle().split(" ")[1].equals(item.getGrand_total().replace("+ Rs. ", ""))) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#cccccc"));
            } else {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            viewHolder1.orderItemAdapter = new com.medicento.retailerappmedi.OrderItemAdapter(item.getOrderItems(), context);
            viewHolder1.order_items_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            viewHolder1.order_items_rv.setAdapter(viewHolder1.orderItemAdapter);

            viewHolder1.order_content.setText(item.getSummary());
            if (item.getSummary().equals("Medicine Purchase")) {
                viewHolder1.order_summary.setText("Invoice No. #"+item.getInvoice_ids());
                viewHolder1.order_amount.setTextColor(Color.parseColor("#05C5CE"));
                viewHolder1.net_due.setText("Rs. " + item.getNet_due());
                viewHolder1.days_ago.setText(item.getDays());
                viewHolder1.amount.setText("Inv Amt "+item.getGrand_total());
                int amount  = (int) Float.parseFloat(item.getGrand_total().replaceAll("- Rs. ", ""));
                double dis = discount*0.01;
                viewHolder1.order_amount.setText("- Rs. "+(int)(amount-(amount*dis)));
                viewHolder1.paid_status.setText(item.getPaid_status());
                viewHolder1.paid_amount.setVisibility(View.VISIBLE);
            } else {
                viewHolder1.paid_amount.setVisibility(View.GONE);
                viewHolder1.net_due.setText("Rs. " + item.getNet_due());
                viewHolder1.order_summary.setText(item.getInvoice_ids());
                viewHolder1.order_amount.setTextColor(Color.parseColor("#ba000d"));
                viewHolder1.order_amount.setText(item.getGrand_total());
            }

            viewHolder1.expanded_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder1.order_items_rv.getVisibility() == View.GONE) {
                        viewHolder1.order_items_rv.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder1.order_items_rv.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        Payment item = payments.get(position);
        if(item.isDate()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return payments != null ? payments.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView order_summary, order_content, order_amount, net_due, paid_status, days_ago, amount;
        RecyclerView order_items_rv;
        ImageView expanded_menu;
        LinearLayout paid_amount;
        com.medicento.retailerappmedi.OrderItemAdapter orderItemAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_summary = itemView.findViewById(R.id.order_summary);
            expanded_menu = itemView.findViewById(R.id.expanded_menu);
            order_content = itemView.findViewById(R.id.order_content);
            paid_status = itemView.findViewById(R.id.paid_status);
            days_ago = itemView.findViewById(R.id.days_ago);
            paid_amount = itemView.findViewById(R.id.paid_amount);
            amount = itemView.findViewById(R.id.amount);
            order_amount = itemView.findViewById(R.id.order_amount);
            order_items_rv = itemView.findViewById(R.id.order_items_rv);
            net_due = itemView.findViewById(R.id.net_due);
        }
    }

    private class DateViewHolder extends ViewHolder {

        TextView date;

        public DateViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
        }
    }
}
