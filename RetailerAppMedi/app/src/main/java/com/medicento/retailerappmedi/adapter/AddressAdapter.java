package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.Address;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<Address> addresses;
    private Context context;

    public AddressAdapter(ArrayList<Address> addresses, Context context) {
        this.addresses = addresses;
        this.context = context;
    }

    private itemClick itemClick;

    public interface itemClick {
        void onItemClick(int position);

        void onRemove(int position);

        void setAddress(int position);
    }

    public void setItemClick(AddressAdapter.itemClick itemClick) {
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return addresses != null ? addresses.size(): 0;
    }

    public class ViewHolder extends BaseViewHolder {

        TextView title_pharmacy, address;
        Button remove, edit;

        public ViewHolder(View itemView) {
            super(itemView);

            title_pharmacy = itemView.findViewById(R.id.title_pharmacy);
            address = itemView.findViewById(R.id.address);

            remove = itemView.findViewById(R.id.remove);
            edit = itemView.findViewById(R.id.edit);
        }

        @Override
        protected void clear() {
            title_pharmacy.setText("");
            address.setText("");
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            title_pharmacy.setText(addresses.get(position).getName());
            address.setText(addresses.get(position).getAddress());

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClick != null) {
                        itemClick.onRemove(getCurrentPosition());
                    }
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClick != null) {
                        itemClick.onItemClick(getCurrentPosition());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClick != null) {
                        itemClick.setAddress(getCurrentPosition());
                    }
                }
            });
        }
    }
}
