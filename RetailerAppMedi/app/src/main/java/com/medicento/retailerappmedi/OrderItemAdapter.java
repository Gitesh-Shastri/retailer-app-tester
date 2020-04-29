package com.medicento.retailerappmedi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import com.medicento.retailerappmedi.data.OrderItem;

import java.util.ArrayList;

public class
OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private ArrayList<OrderItem> orderItems;
    private Context context;

    private boolean isOnReturnScreen;
    private boolean isDisabled;

    public boolean isOnReturnScreen() {
        return isOnReturnScreen;
    }

    public void setOnReturnScreen(boolean onReturnScreen) {
        isOnReturnScreen = onReturnScreen;
    }

    public OrderItemAdapter(ArrayList<OrderItem> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
        this.isOnReturnScreen = false;
    }


    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick1(int position, boolean checked);
    }

    public void setOnItemClicklistener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_order_detail,viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        holder.selected_medicine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                orderItems.get(i).setReturned(b);
                if(mListener != null) {
                    mListener.onItemClick1(holder.getAdapterPosition(), b);
                }
            }
        });

        holder.dispute.setChecked(orderItems.get(i).isDispute());

        holder.dispute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                orderItems.get(i).setDispute(b);
            }
        });

        if (orderItems.get(i).isIs_executed()) {
            holder.selected_medicine.setEnabled(false);
        }

        holder.medicine_name.setText((i+1) + ". " + orderItems.get(i).getName());
        holder.company_name.setText(orderItems.get(i).getCompany());
        holder.price.setText(" Rs. "+orderItems.get(i).getPrice());
        holder.qty.setText(String.format("%10s", "Qty: "+orderItems.get(i).getQty()));

        holder.selected_medicine.setChecked(orderItems.get(i).isReturned());

        if(orderItems.get(i).isAlreadyReturned()) {
            holder.selected_medicine.setEnabled(false);
        }

        holder.packing.setText(orderItems.get(i).getScheme() +" " + String.format("%10s", orderItems.get(i).getPacking()));

        holder.issue_type.setVisibility(View.VISIBLE);
        holder.reason.setVisibility(View.GONE);
        holder.dispute.setVisibility(View.GONE);
        holder.issue_type.setText("Missed");

        holder.reason_dispute.setVisibility(View.GONE);

        if (orderItems.get(i).getReason().equals("-")) {
            if (orderItems.get(i).isReturned()) {
                holder.issue_type.setVisibility(View.GONE);
                holder.issue_type.setText("");
            } else if ((orderItems.get(i).getQty() - orderItems.get(i).getBounce_qty()) > 0) {
                holder.issue_type.setText("Bounced");
            }
        } else {
            holder.issue_type.setText("Issues");
            holder.reason.setVisibility(View.VISIBLE);
            holder.dispute.setVisibility(View.VISIBLE);
            holder.reason_dispute.setVisibility(View.VISIBLE);
        }
        holder.reason.setText(orderItems.get(i).getReason());

        if (!orderItems.get(i).getDispute().equals("-")) {
            holder.reason_dispute.setText(orderItems.get(i).getDispute());
        }

        holder.reason_dispute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(holder.reason_dispute.hasFocus()) {
                    orderItems.get(i).setDispute(holder.reason_dispute.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox selected_medicine;
        TextView medicine_name, company_name, qty, price,packing, reason, issue_type;
        Switch dispute;
        EditText reason_dispute;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicine_name = itemView.findViewById(R.id.medicine_name);
            company_name = itemView.findViewById(R.id.medicine_company);
            qty = itemView.findViewById(R.id.medicine_quantity);
            packing = itemView.findViewById(R.id.packing);
            price = itemView.findViewById(R.id.medicine_cost);
            selected_medicine = itemView.findViewById(R.id.selected_medicine);
            reason = itemView.findViewById(R.id.reason);
            dispute = itemView.findViewById(R.id.dispute);
            reason_dispute = itemView.findViewById(R.id.reason_dispute);
            issue_type = itemView.findViewById(R.id.issue_type);
        }
    }
}
