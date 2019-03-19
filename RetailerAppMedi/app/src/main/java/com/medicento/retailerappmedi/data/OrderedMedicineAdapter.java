package com.medicento.retailerappmedi.data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.medicento.retailerappmedi.PlaceOrderActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Register;
import com.medicento.retailerappmedi.SignUpActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderedMedicineAdapter extends RecyclerView.Adapter<OrderedMedicineAdapter.MedicineViewHolder> implements Serializable{

    public ArrayList<OrderedMedicine> mMedicinesList;
    private Context context;
    public float mOverallCost = 0;
    OverallCostChangeListener mOverallCostChangeListener;

    public OrderedMedicineAdapter(ArrayList<OrderedMedicine> list) {
        mMedicinesList = list;
    }

    public void setOverallCostChangeListener(OverallCostChangeListener listener) {
        this.mOverallCostChangeListener = listener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_order_medicine, parent, false);

        MedicineViewHolder viewHolder = new MedicineViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        holder.bind(position);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mMedicinesList.size();
    }

    public void add(OrderedMedicine medicine) {
        for (OrderedMedicine med: mMedicinesList) {
            if (medicine.getMedicineName().equals(med.getMedicineName())) {
                if (medicine.getMedicineCompany().equals(med.getMedicineCompany())) {
                    int qty = med.getQty();
                    int stock = med.getStock();
                    qty++;
                    float cost = med.getRate()*qty;
                    med.setQty(qty);
                    med.setStock(stock);
                    med.setCost(cost);
                    mOverallCost += medicine.getCost();
                    notifyDataSetChanged();
                    return;
                }
            }
        }
        mMedicinesList.add(0, medicine);
        mOverallCost += medicine.getCost();
        notifyItemInserted(0);
    }

    public void remove(int pos) {
        float cost = mMedicinesList.get(pos).getCost();
        mOverallCost -= cost;
        notifyDataSetChanged();
        notifyDataSetChanged();
        if (mOverallCostChangeListener != null) mOverallCostChangeListener.onCostChanged(mOverallCost,mMedicinesList.get(pos).getQty(), "sub");
        mMedicinesList.remove(pos);
        notifyDataSetChanged();
    }

    public void reset() {
        mMedicinesList.clear();
        mOverallCost = 0;
        notifyDataSetChanged();
    }


    public ArrayList<OrderedMedicine> getList() {
        return mMedicinesList;
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        AlertDialog alert;

        TextView MedName, MedCompany, MedQty, MedCost, incQty, decQty, MedRate,stock, scheme;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            MedName = itemView.findViewById(R.id.medicine_name);
            MedCompany = itemView.findViewById(R.id.medicine_company);
            MedRate = itemView.findViewById(R.id.medicine_rate);
            MedQty = itemView.findViewById(R.id.medicine_qty);
            MedCost = itemView.findViewById(R.id.medicine_cost);
            incQty = itemView.findViewById(R.id.inc_qty);
            incQty.setOnClickListener(this);
            decQty = itemView.findViewById(R.id.dec_qty);
            decQty.setOnClickListener(this);
            stock = itemView.findViewById(R.id.stcok);
            scheme = itemView.findViewById(R.id.scheme);
        }
        public void bind(int pos) {
            OrderedMedicine medicine = mMedicinesList.get(pos);
            MedName.setText(medicine.getMedicineName());
            MedCompany.setText(medicine.getMedicineCompany());
            MedRate.setText("PTR : "+"\u20B9"+medicine.getCost() + "");
            MedCost.setText("Packing : "+medicine.getPacking() + "");
            MedQty.setText(medicine.getQty() + "");
            stock.setText("Stock" + medicine.getStock());
            scheme.setText("Scheme : " + medicine.getScheme());
        }

        @Override
        public void onClick(View v) {
           int pos = getAdapterPosition();
           if (v.getId() == R.id.inc_qty) {
               for (int i = 0; i < mMedicinesList.size(); i++) {
                   OrderedMedicine med = mMedicinesList.get(i);
                   if (med.getMedicineName().equals(mMedicinesList.get(pos).getMedicineName())) {
                       if(med.getStock() <= 0) {

                           final Dialog dialog1 = new Dialog(context);
                           dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                           dialog1.setContentView(R.layout.maximum_limit);

                           Button back1 = dialog1.findViewById(R.id.okay);
                           back1.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   dialog1.dismiss();
                               }
                           });

                           dialog1.show();

                           return;
                       }
                       int qty = med.getQty();
                       int stock = med.getStock();
                       stock--;
                       qty++;
                       float cost = med.getRate()*qty;
                       med.setQty(qty);
                       med.setCost(cost);
                       med.setStock(stock);
                       mMedicinesList.set(i, med);
                       mOverallCost += med.getRate();
                       if (mOverallCostChangeListener != null) mOverallCostChangeListener.onCostChanged(mOverallCost,1, "add");
                       notifyDataSetChanged();
                       return;
                   }
               }
           } else {
               for (int i = 0; i < mMedicinesList.size(); i++) {
                   OrderedMedicine med = mMedicinesList.get(i);
                   if (med.getMedicineName().equals(mMedicinesList.get(pos).getMedicineName())) {
                       int qty = med.getQty();
                       int stock = med.getStock();
                       stock++;
                       qty--;
                       mOverallCost -= med.getRate();
                       if (qty == 0) {
                           mMedicinesList.remove(i);
                           notifyDataSetChanged();
                           if (mOverallCostChangeListener != null) mOverallCostChangeListener.onCostChanged(mOverallCost, 1,"sub");
                           return;
                       }
                       med.setStock(stock);
                       float cost = med.getRate()*qty;
                       med.setQty(qty);
                       med.setCost(cost);
                       mMedicinesList.set(i, med);
                       notifyDataSetChanged();
                       return;
                   }
               }
           }
        }
    }


    public float getOverallCost() {
        return mOverallCost;
    }

    public interface OverallCostChangeListener {
        void onCostChanged(float newCost, int qty,String type);
    }

}