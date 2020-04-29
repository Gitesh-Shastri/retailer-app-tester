package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.Medicine;
import com.medicento.retailerappmedi.data.MedicineOrdered;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchMedicineAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<Medicine> medicineOrdereds;

    public SearchMedicineAdapter(Context context, ArrayList<Medicine> medicineOrdereds) {
        this.context = context;
        this.medicineOrdereds = medicineOrdereds;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(int positon);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ordered_medicines, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    @Override
    public int getItemCount() {
        return medicineOrdereds != null ? medicineOrdereds.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        TextView name, scheme, company_name, packing, price;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            scheme = itemView.findViewById(R.id.scheme);
            company_name = itemView.findViewById(R.id.company_name);
            packing = itemView.findViewById(R.id.packing);
            price = itemView.findViewById(R.id.price);
        }

        @Override
        protected void clear() {
            name.setText("");
            scheme.setText("");
            company_name.setText("");
            packing.setText("");
            price.setText("");
        }

        @Override
        public void onBind(final int position) {
            super.onBind(position);

            Medicine medicine = medicineOrdereds.get(position);

            name.setText(medicine.getMedicentoName());
            scheme.setText(medicine.getScheme());
            company_name.setText(medicine.getCompanyName());
            packing.setText(medicine.getPacking());
            price.setText("₹ "+medicine.getmPrice());

            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f0f0f0"));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(position);
                    }
                }
            });
        }
    }
}
