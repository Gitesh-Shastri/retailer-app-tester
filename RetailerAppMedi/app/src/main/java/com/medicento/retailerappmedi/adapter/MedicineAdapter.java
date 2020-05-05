package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.OrderedMedicine;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<OrderedMedicine> medicines;
    private Context context;
    public float mOverallCost = 0;

    public MedicineAdapter(ArrayList<OrderedMedicine> medicines, Context context) {
        this.medicines = medicines;
        this.context = context;
    }

    OverallCostChangeListener mOverallCostChangeListener;

    public float getTotalCost() {
        Float cost = 0f;
        for (OrderedMedicine med: medicines) {
            cost += med.getCost();
        }
        return cost;
    }

    public interface OverallCostChangeListener {
        void onCostChanged();
    }

    public void setmOverallCostChangeListener(OverallCostChangeListener mOverallCostChangeListener) {
        this.mOverallCostChangeListener = mOverallCostChangeListener;
    }

    public ArrayList<OrderedMedicine> getMedicines() {
        return medicines;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_medicine, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    @Override
    public int getItemCount() {
        return medicines != null ? medicines.size() : 0;
    }

    public void add(OrderedMedicine medicine) {
        for (OrderedMedicine med: medicines) {
            if (medicine.getMedicineName().equals(med.getMedicineName())) {
                if (medicine.getMedicineCompany().equals(med.getMedicineCompany())) {
                    int qty = med.getQty();
                    int stock = med.getStock();
                    stock--;
                    qty++;
                    float cost = med.getRate()*qty;
                    med.setQty(qty);
                    med.setStock(stock);
                    med.setCost(cost);
                    mOverallCost += medicine.getCost();
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyDataSetChanged();
                    return;
                }
            }
        }
        medicine.setStock(medicine.getStock()-1);
        mOverallCost += medicine.getCost();
        medicines.add(0, medicine);
        if (mOverallCostChangeListener != null) {
            mOverallCostChangeListener.onCostChanged();
        }
        notifyItemInserted(0);
    }

    public void sub(OrderedMedicine medicine) {
        for (int i=0;i<medicines.size();i++) {
            OrderedMedicine med = medicines.get(i);
            if (medicine.getMedicineName().equals(med.getMedicineName())) {
                if (medicine.getMedicineCompany().equals(med.getMedicineCompany())) {
                    int qty = med.getQty();
                    int stock = med.getStock();
                    stock++;
                    qty--;
                    if (qty == 0) {
                        mOverallCost -= medicine.getCost();
                        medicines.remove(i);
                        if (mOverallCostChangeListener != null) {
                            mOverallCostChangeListener.onCostChanged();
                        }
                        notifyDataSetChanged();
                        return;
                    }
                    float cost = med.getRate()*qty;
                    med.setQty(qty);
                    med.setStock(stock);
                    med.setCost(cost);
                    mOverallCost -= medicine.getCost();
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyDataSetChanged();
                    return;
                }
            }
        }
        for (OrderedMedicine med: medicines) {
        }
    }

    public class ViewHolder extends BaseViewHolder {

        TextView name, scheme, company, ptr, packing, qty;
        ImageView add, sub;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            scheme = itemView.findViewById(R.id.scheme);
            company = itemView.findViewById(R.id.company);
            ptr = itemView.findViewById(R.id.ptr);
            qty = itemView.findViewById(R.id.qty);
            add = itemView.findViewById(R.id.add);
            sub = itemView.findViewById(R.id.sub);
            packing = itemView.findViewById(R.id.packing);
        }

        @Override
        protected void clear() {
            name.setText("");
            scheme.setText("");
            company.setText("");
            ptr.setText("");
            packing.setText("");
            qty.setText("");
        }

        @Override
        public void onBind(final int position) {
            super.onBind(position);

            final OrderedMedicine orderedMedicine = medicines.get(position);

            name.setText(orderedMedicine.getMedicineName());
            company.setText(orderedMedicine.getMedicineCompany());
            ptr.setText("PTR : ₹ "+orderedMedicine.getRate());
            packing.setText("Packing : "+orderedMedicine.getPacking());
            scheme.setText("Scheme : " + orderedMedicine.getOffer_qty());
            qty.setText(orderedMedicine.getQty()+"");
            try {
                if(orderedMedicine.getScheme().isEmpty()) {
                    if(!orderedMedicine.getDiscount().isEmpty()) {
                        scheme.setText("Scheme : "+orderedMedicine.getOffer_qty() +"");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add(medicines.get(position));
                }
            });

            sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sub(medicines.get(position));
                }
            });
        }
    }
}
