package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.order_related.OrderItem;

import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private ArrayList<OrderItem> orderItems;
    private Context context;

    private boolean isOnReturnScreen;

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
        View view = inflater.inflate(R.layout.item_order_details,viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        holder.selected_medicine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mListener != null) {
                    mListener.onItemClick1(holder.getAdapterPosition(), b);
                }
            }
        });
        holder.medicine_name.setText(orderItems.get(i).getName());
        holder.company_name.setText(orderItems.get(i).getCompany());
        holder.price.setText(" Rs. "+orderItems.get(i).getPrice());
        holder.qty.setText("Qty: "+orderItems.get(i).getQty());

        if(isOnReturnScreen) {
            holder.reason_rl.setVisibility(View.VISIBLE);
            holder.selected_medicine.setChecked(orderItems.get(i).isReturned());
        } else {
            holder.reason_rl.setVisibility(View.GONE);
        }

        if(orderItems.get(i).isAlreadyReturned()) {
            holder.returned.setVisibility(View.VISIBLE);
            holder.selected_medicine.setEnabled(false);
            holder.reason.setEnabled(false);
        } else {
            holder.returned.setVisibility(View.GONE);
        }

        holder.reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {
                orderItems.get(i).setReason(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(!orderItems.get(i).getReason().isEmpty()) {
            try {
                ArrayAdapter myAdap = (ArrayAdapter) holder.reason.getAdapter();
                int spinnerPosition = myAdap.getPosition(orderItems.get(i).getReason());
                holder.reason.setSelection(spinnerPosition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox selected_medicine;
        TextView medicine_name, company_name, qty, price, returned;
        RelativeLayout reason_rl;
        Spinner reason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicine_name = itemView.findViewById(R.id.medicine_name);
            company_name = itemView.findViewById(R.id.medicine_company);
            qty = itemView.findViewById(R.id.medicine_quantity);
            returned = itemView.findViewById(R.id.returned);
            price = itemView.findViewById(R.id.medicine_cost);
            selected_medicine = itemView.findViewById(R.id.selected_medicine);

            reason = itemView.findViewById(R.id.reason);
            reason_rl = itemView.findViewById(R.id.reason_rl);
        }
    }
}
