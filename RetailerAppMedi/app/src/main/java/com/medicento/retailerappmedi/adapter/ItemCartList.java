package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.EssentialList;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ItemCartList extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<EssentialList> essentialLists;
    private Context context;

    public ItemCartList(ArrayList<EssentialList> essentialLists, Context context) {
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
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_cart_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    public ArrayList<EssentialList> getEssentialLists() {
        return essentialLists;
    }

    @Override
    public int getItemCount() {
        return essentialLists != null ? essentialLists.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        EditText qty;
        TextView cost, name;
        ImageView edit, delete;

        public ViewHolder(View itemView) {
            super(itemView);

            edit = itemView.findViewById(R.id.edit);
            qty = itemView.findViewById(R.id.qty);
            name = itemView.findViewById(R.id.name);
            cost = itemView.findViewById(R.id.cost);
            delete = itemView.findViewById(R.id.delete);
        }

        @Override
        protected void clear() {
            qty.setText("");
            cost.setText("");
            name.setText("");
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            EssentialList essentialList = essentialLists.get(position);

            qty.setText(essentialList.getQty()+"");
            cost.setText((essentialList.getCost()*essentialList.getQty())+"");

            name.setText(essentialList.getName());

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qty.requestFocus();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    essentialLists.get(getCurrentPosition()).setQty(0);
                    if (mOverallCostChangeListener != null) {
                        mOverallCostChangeListener.onCostChanged();
                    }
                    notifyItemRemoved(getCurrentPosition());
                }
            });

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
        }
    }
}
