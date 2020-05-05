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
import com.medicento.retailerappmedi.data.MedicineOrdered;

import java.util.ArrayList;

public class MedicinesOrderedReturnAdapter extends RecyclerView.Adapter<MedicinesOrderedReturnAdapter.ViewHolder>{

    private ArrayList<MedicineOrdered> medicineOrdereds;
    private Context context;

    public MedicinesOrderedReturnAdapter(ArrayList<MedicineOrdered> medicineOrdereds, Context context) {
        this.medicineOrdereds = medicineOrdereds;
        this.context = context;
    }

    public ArrayList<MedicineOrdered> getMedicineOrdereds() {
        return medicineOrdereds;
    }

    public void setMedicineOrdereds(ArrayList<MedicineOrdered> medicineOrdereds) {
        this.medicineOrdereds = medicineOrdereds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_to_return, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.name.setText(medicineOrdereds.get(i).getName());
        viewHolder.qty.append(medicineOrdereds.get(i).getQuantity()+"");
        viewHolder.company.append(medicineOrdereds.get(i).getManufact_name());
        viewHolder.price.append(medicineOrdereds.get(i).getPrice()+"");

        if(medicineOrdereds.get(i).isAlreadyReturned()) {
            viewHolder.name.setEnabled(false);
            viewHolder.reason.setEnabled(false);
        }

        viewHolder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    viewHolder.reason_rl.setBackgroundResource(R.drawable.border_curved_active);
                } else {
                    viewHolder.reason_rl.setBackgroundResource(R.drawable.border_curved_non_active);
                }
                medicineOrdereds.get(i).setReturned(b);
            }
        });

        viewHolder.name.setChecked(medicineOrdereds.get(i).isReturned());

        viewHolder.reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {
                medicineOrdereds.get(i).setReason(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(!medicineOrdereds.get(i).getReason().isEmpty()) {
           try {
               ArrayAdapter myAdap = (ArrayAdapter) viewHolder.reason.getAdapter();
               int spinnerPosition = myAdap.getPosition(medicineOrdereds.get(i).getReason());
               viewHolder.reason.setSelection(spinnerPosition);
           } catch (Exception e) {
               e.printStackTrace();
           }
        }
    }

    @Override
    public int getItemCount() {
        return medicineOrdereds != null ? medicineOrdereds.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox name;
        TextView qty, company, price;
        RelativeLayout reason_rl;
        Spinner reason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
            company = itemView.findViewById(R.id.company);

            price = itemView.findViewById(R.id.price);
            reason = itemView.findViewById(R.id.reason);
            reason_rl = itemView.findViewById(R.id.reason_rl);
        }
    }
}
