package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.EssentialList;

import java.util.ArrayList;

public class PaymentCartAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<EssentialList> essentialLists;
    private Context context;

    public PaymentCartAdapter(ArrayList<EssentialList> essentialLists, Context context) {
        this.essentialLists = essentialLists;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_payment_cart, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    @Override
    public int getItemCount() {
        return essentialLists != null ? essentialLists.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        TextView name, qty, price;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
            price = itemView.findViewById(R.id.price);

            image = itemView.findViewById(R.id.image);
        }

        @Override
        protected void clear() {
            name.setText("");
            qty.setText("");
            price.setText("");
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            EssentialList essentialList = essentialLists.get(position);

            name.setText(essentialList.getName());
            qty.setText(essentialList.getQty()+"");
            price.setText("₹ " +(essentialList.getQty()*essentialList.getCost()));

            Glide.with(context).load(essentialList.getImage_url()).into(image);
        }
    }
}
