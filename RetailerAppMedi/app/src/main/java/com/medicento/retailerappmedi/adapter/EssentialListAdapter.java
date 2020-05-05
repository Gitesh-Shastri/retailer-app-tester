package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.activity.ParticularOrderActivity;
import com.medicento.retailerappmedi.data.EssentialList;

import java.util.ArrayList;

public class EssentialListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<EssentialList> essentialLists;
    private Context context;

    public EssentialListAdapter(ArrayList<EssentialList> essentialLists, Context context) {
        this.essentialLists = essentialLists;
        this.context = context;
    }

    OverallCostChangeListener mOverallCostChangeListener;

    public interface OverallCostChangeListener {
        void onCostChanged();
    }

    public void setmOverallCostChangeListener(OverallCostChangeListener mOverallCostChangeListener) {
        this.mOverallCostChangeListener = mOverallCostChangeListener;
    }

    public ArrayList<EssentialList> getEssentialLists() {
        return essentialLists;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_essential_list, viewGroup, false);

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

        TextView old_price, name, new_price;
        ImageView delete, edit, image;
        Button cart;
        LinearLayout add_cart, details;
        EditText qty;

        public ViewHolder(View itemView) {
            super(itemView);

            old_price = itemView.findViewById(R.id.old_price);
            new_price = itemView.findViewById(R.id.new_price);
            add_cart = itemView.findViewById(R.id.add_cart);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
            cart = itemView.findViewById(R.id.cart);
            name = itemView.findViewById(R.id.name);
            details = itemView.findViewById(R.id.details);
            image = itemView.findViewById(R.id.image);
            qty = itemView.findViewById(R.id.qty);
        }

        @Override
        protected void clear() {
            name.setText("");
            add_cart.setVisibility(View.GONE);
        }

        @Override
        public void onBind(final int position) {
            super.onBind(position);
            old_price.setPaintFlags(old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            name.setText(essentialLists.get(position).getName());
            qty.setText(essentialLists.get(position).getQty()+"");
            old_price.setText(essentialLists.get(position).getCost()+"");
            new_price.setText(essentialLists.get(position).getCost()+"");

            Glide.with(context).load(essentialLists.get(position).getImage_url()).into(image);

            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    essentialLists.get(getCurrentPosition()).setQty(1);
                    cart.setVisibility(View.GONE);
                    add_cart.setVisibility(View.VISIBLE);
                    qty.requestFocus();
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyItemChanged(getCurrentPosition());
                }
            });

            if (essentialLists.get(position).getQty() > 0) {
                cart.setVisibility(View.GONE);
                add_cart.setVisibility(View.VISIBLE);
            } else {
                cart.setVisibility(View.VISIBLE);
                add_cart.setVisibility(View.GONE);
            }

            qty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        essentialLists.get(getCurrentPosition()).setQty(Integer.parseInt(qty.getText().toString()));
                        if (mOverallCostChangeListener != null) {
                            mOverallCostChangeListener.onCostChanged();
                        }
                        notifyItemChanged(getCurrentPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    qty.setSelection(qty.getText().toString().length());
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    essentialLists.get(getCurrentPosition()).setQty(0);
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyItemChanged(getCurrentPosition());
                }
            });

            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ParticularOrderActivity.class).putExtra("item", essentialLists.get(getCurrentPosition())));
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ParticularOrderActivity.class).putExtra("item", essentialLists.get(getCurrentPosition())));
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qty.requestFocus();
                }
            });
        }
    }
}
