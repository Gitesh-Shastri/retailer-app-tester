package com.example.salestargetapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salestargetapp.R;
import com.example.salestargetapp.dashboard.DashboardActivity;
import com.example.salestargetapp.data.Pharmacy;

import java.util.ArrayList;

public class PharmacySelectionAdapter extends RecyclerView.Adapter<PharmacySelectionAdapter.ViewHolder> {

    private ArrayList<Pharmacy> pharmacies;
    private Context context;

    public PharmacySelectionAdapter(ArrayList<Pharmacy> pharmacies, Context context) {
        this.pharmacies = pharmacies;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_pharmacy, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Pharmacy pharmacy = pharmacies.get(position);

        holder.pharmacy_name.setText(pharmacy.getName());
        holder.pharmacy_code.setText(pharmacy.getPharma_code());
        holder.pharmacy_no.setText(pharmacy.getMobile_no());
    }

    @Override
    public int getItemCount() {
        return pharmacies != null ? pharmacies.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pharmacy_name, pharmacy_code, pharmacy_no;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pharmacy_name = itemView.findViewById(R.id.pharmacy_name);
            pharmacy_code = itemView.findViewById(R.id.pharmacy_code);
            pharmacy_no = itemView.findViewById(R.id.pharmacy_no);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, DashboardActivity.class));
                }
            });
        }
    }
}
