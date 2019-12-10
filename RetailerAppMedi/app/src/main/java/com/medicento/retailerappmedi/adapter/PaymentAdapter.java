package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.OrderAdapterDelivered;
import com.medicento.retailerappmedi.data.order_related.Payment;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder>{

    private ArrayList<Payment> payments;
    private Context context;

    public PaymentAdapter(ArrayList<Payment> payments, Context context) {
        this.payments = payments;
        this.context = context;
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
            ViewHolder viewHolder1 = (ViewHolder) viewHolder;
            viewHolder1.order_content.setText(item.getContent());
            viewHolder1.order_summary.setText(item.getOrder_id());
            viewHolder.order_amount.setText("Rs. " + item.getSummary());
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

        TextView order_summary, order_content, order_amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_summary = itemView.findViewById(R.id.order_summary);
            order_content = itemView.findViewById(R.id.order_content);
            order_amount = itemView.findViewById(R.id.order_amount);
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
