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

    private RecyclerView recyclerView;
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

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
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

        TextView old_price, name, new_price, qty_per_100, qty_per_200, qty_per_500, qty_per_1000, qty_per_10000;
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
            qty_per_100 = itemView.findViewById(R.id.qty_per_100);
            qty_per_200 = itemView.findViewById(R.id.qty_per_200);
            qty_per_500 = itemView.findViewById(R.id.qty_per_500);
            qty_per_1000 = itemView.findViewById(R.id.qty_per_1000);
            qty_per_10000= itemView.findViewById(R.id.qty_per_10000);
        }

        @Override
        protected void clear() {
            name.setText("");
            qty_per_100.setText("");
            qty_per_200.setText("");
            qty_per_500.setText("");
            qty_per_1000.setText("");
            qty_per_10000.setText("");
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


            qty_per_100.setText("100 Qty - " + essentialLists.get(position).getCost_100() + " /PC");
            qty_per_500.setText("500 Qty - " + essentialLists.get(position).getCost_500() + " /PC");
            qty_per_200.setText("200 Qty - " + essentialLists.get(position).getCost_200() + " /PC");
            qty_per_1000.setText("1000 Qty - " + essentialLists.get(position).getCost_1000() + " /PC");
            qty_per_10000.setText("10000 Qty - " + essentialLists.get(position).getCost_10000() + " /PC");

            qty_per_100.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    essentialLists.get(getCurrentPosition()).setQty(100);
                    cart.setVisibility(View.GONE);
                    add_cart.setVisibility(View.VISIBLE);
                    qty.requestFocus();
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyItemChanged(getCurrentPosition());
                }
            });

            qty_per_200.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    essentialLists.get(getCurrentPosition()).setQty(200);
                    cart.setVisibility(View.GONE);
                    add_cart.setVisibility(View.VISIBLE);
                    qty.requestFocus();
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyItemChanged(getCurrentPosition());
                }
            });

            qty_per_500.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    essentialLists.get(getCurrentPosition()).setQty(500);
                    cart.setVisibility(View.GONE);
                    add_cart.setVisibility(View.VISIBLE);
                    qty.requestFocus();
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyItemChanged(getCurrentPosition());
                }
            });

            qty_per_1000.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    essentialLists.get(getCurrentPosition()).setQty(1000);
                    cart.setVisibility(View.GONE);
                    add_cart.setVisibility(View.VISIBLE);
                    qty.requestFocus();
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyItemChanged(getCurrentPosition());
                }
            });

            qty_per_10000.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    essentialLists.get(getCurrentPosition()).setQty(10000);
                    cart.setVisibility(View.GONE);
                    add_cart.setVisibility(View.VISIBLE);
                    qty.requestFocus();
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyItemChanged(getCurrentPosition());
                }
            });

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
