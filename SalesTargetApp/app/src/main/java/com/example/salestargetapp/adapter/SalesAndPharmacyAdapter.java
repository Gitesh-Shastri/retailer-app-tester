package com.example.salestargetapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salestargetapp.R;
import com.example.salestargetapp.data.PharmacyWithSales;

import java.util.ArrayList;

public class SalesAndPharmacyAdapter extends RecyclerView.Adapter<SalesAndPharmacyAdapter.ViewHolder>{

    private ArrayList<PharmacyWithSales> pharmacyWithSales;
    private Context context;

    public SalesAndPharmacyAdapter(ArrayList<PharmacyWithSales> pharmacyWithSales, Context context) {
        this.pharmacyWithSales = pharmacyWithSales;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.iten_pharmacy_with_sales, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.pharmacy_name.setText(pharmacyWithSales.get(position).getName());
        holder.pharmacy_sales.setText(pharmacyWithSales.get(position).getSales());
    }

    @Override
    public int getItemCount() {
        return pharmacyWithSales != null ? pharmacyWithSales.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pharmacy_name, pharmacy_sales;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pharmacy_name = itemView.findViewById(R.id.pharmacy_name);
            pharmacy_sales = itemView.findViewById(R.id.pharmacy_sales);
        }
    }
}
