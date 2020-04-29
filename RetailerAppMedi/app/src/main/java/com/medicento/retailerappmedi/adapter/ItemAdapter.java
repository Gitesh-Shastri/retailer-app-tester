package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.OrderedMedicine;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<OrderedMedicine> orderedMedicines;
    private Context context;

    public ItemAdapter(ArrayList<OrderedMedicine> orderedMedicines, Context context) {
        this.orderedMedicines = orderedMedicines;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_details, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    @Override
    public int getItemCount() {
        return orderedMedicines != null ? orderedMedicines.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        TextView name, s_no, qty, price;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            s_no = itemView.findViewById(R.id.s_no);
            price = itemView.findViewById(R.id.price);
            qty = itemView.findViewById(R.id.qty);
        }

        @Override
        protected void clear() {
            name.setText("");
            s_no.setText("");
            price.setText("");
            qty.setText("");
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            OrderedMedicine orderedMedicine = orderedMedicines.get(position);

            name.setText(orderedMedicine.getMedicineName()+"\n"+orderedMedicine.getMedicineCompany());
            s_no.setText(position+"");
            price.setText("₹ "+orderedMedicine.getmCost());
            qty.setText(orderedMedicine.getQty()+"");
        }
    }
}
