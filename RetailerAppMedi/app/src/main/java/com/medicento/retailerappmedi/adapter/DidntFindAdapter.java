package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.DidntFind;

import java.util.ArrayList;

public class DidntFindAdapter extends RecyclerView.Adapter<DidntFindAdapter.ViewHolder>{

    private ArrayList<DidntFind> didntFinds;
    private Context context;
    private boolean hideQtyButton;

    public DidntFindAdapter(ArrayList<DidntFind> didntFinds, Context context, boolean hideQtyButton) {
        this.didntFinds = didntFinds;
        this.context = context;
        this.hideQtyButton = hideQtyButton;
    }

    public DidntFindAdapter(ArrayList<DidntFind> didntFinds, Context context) {
        this.didntFinds = didntFinds;
        this.context = context;
        this.hideQtyButton = false;
    }

    public ArrayList<DidntFind> getDidntFinds() {
        return didntFinds;
    }

    public void setDidntFinds(ArrayList<DidntFind> didntFinds) {
        this.didntFinds = didntFinds;
    }

    public boolean isHideQtyButton() {
        return hideQtyButton;
    }

    public void setHideQtyButton(boolean hideQtyButton) {
        this.hideQtyButton = hideQtyButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_didnt_find, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        DidntFind didntFind = didntFinds.get(i);

        viewHolder.name.setText(didntFind.getName());
        viewHolder.medicine_qty.setText(didntFind.getQuantity()+"");

        if(hideQtyButton) {
            viewHolder.dec_qty.setVisibility(View.GONE);
            viewHolder.inc_qty.setVisibility(View.GONE);
            viewHolder.medicine_qty.setText("Qty: " + didntFind.getQuantity());
        }
    }

    @Override
    public int getItemCount() {
        return didntFinds != null ? didntFinds.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, medicine_qty, dec_qty, inc_qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            medicine_qty = itemView.findViewById(R.id.medicine_qty);

            dec_qty = itemView.findViewById(R.id.dec_qty);
            inc_qty = itemView.findViewById(R.id.inc_qty);

            dec_qty.setOnClickListener(this);
            inc_qty.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if (view.getId() == R.id.inc_qty) {
                didntFinds.get(pos).setQuantity(didntFinds.get(pos).getQuantity()+1);
                notifyItemChanged(pos);
            }
            if (view.getId() == R.id.dec_qty) {
                if(didntFinds.get(pos).getQuantity() > 1 ) {
                    didntFinds.get(pos).setQuantity(didntFinds.get(pos).getQuantity()-1);
                    notifyItemChanged(pos);
                    return;
                }
                didntFinds.remove(pos);
                notifyItemRemoved(pos);
            }
        }
    }

}
