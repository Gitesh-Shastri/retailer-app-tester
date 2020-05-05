package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.Medicine;

import java.util.ArrayList;

public class MedicineSearchAdapter extends RecyclerView.Adapter<MedicineSearchAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Medicine> medicineResponses;

    public MedicineSearchAdapter(Context context, ArrayList<Medicine> medicineResponses) {
        this.context = context;
        this.medicineResponses = medicineResponses;
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick1(int position);
    }

    public void setOnItemClicklistener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MedicineSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.medicine_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Medicine medicineAuto = medicineResponses.get(i);

        viewHolder.name.setText(medicineAuto.getMedicentoName());
        viewHolder.company.setText("Company : " + medicineAuto.getCompanyName());
        viewHolder.mrp.setText("\u20B9" + medicineAuto.getmPrice() + "");
        if (!medicineAuto.getDiscount().isEmpty()) {
            viewHolder.medicine_discount.setText("" + medicineAuto.getOffer_qty() + "");
        } else {
            viewHolder.medicine_discount.setText("-");
        }
        if (!medicineAuto.getPacking().isEmpty()) {
            viewHolder.packing.setText(medicineAuto.getPacking());
        } else {
            viewHolder.packing.setText(" - ");
        }
    }

    @Override
    public int getItemCount() {
        return medicineResponses != null ? medicineResponses.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, company, mrp, medicine_discount, packing;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);

            name = convertView.findViewById(R.id.medicine_name_auto);
            company = convertView.findViewById(R.id.company_name_auto);
            mrp = convertView.findViewById(R.id.medicine_mrp_auto);
            medicine_discount = convertView.findViewById(R.id.medicine_discount);
            packing = convertView.findViewById(R.id.packing);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mListener != null && position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick1(position);
                    }
                }
            });
        }
    }
}
