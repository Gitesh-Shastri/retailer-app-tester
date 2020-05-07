package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.EssentialList;

import java.util.ArrayList;

public class OrderItemEssentialAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<EssentialList> essentialLists;
    private Context context;

    public OrderItemEssentialAdapter(ArrayList<EssentialList> essentialLists, Context context) {
        this.essentialLists = essentialLists;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_essential_order, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return essentialLists != null ? essentialLists.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        TextView s_no, name, qty;

        public ViewHolder(View itemView) {
            super(itemView);

            s_no = itemView.findViewById(R.id.s_no);
            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
        }

        @Override
        protected void clear() {
            s_no.setText("");
            name.setText("");
            qty.setText("");
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            EssentialList essentialList = essentialLists.get(position);

            s_no.setText((position+1)+".");
            name.setText(essentialList.getName());
            qty.setText("Qty = " + essentialList.getQty());
        }
    }
}
