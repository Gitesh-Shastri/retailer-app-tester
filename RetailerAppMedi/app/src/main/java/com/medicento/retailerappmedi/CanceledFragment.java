package com.medicento.retailerappmedi;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medicento.retailerappmedi.data.OrderAdapterDelivered;
import com.medicento.retailerappmedi.data.RecentOrderDelivered;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CanceledFragment extends Fragment{
    private static String url = "";

    private static OrderAdapterDelivered mOrder;
    RecyclerView iv;
    View view;

    private Context context;

    public CanceledFragment() {
        // Required empty public constructor
    }

    ArrayList<RecentOrderDelivered> orders;

    public void addOrders(ArrayList<RecentOrderDelivered> orders) {
        this.orders = orders;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_canceled, container, false);
                iv = (RecyclerView) view.findViewById(R.id.listview3);
                iv.setLayoutManager(new LinearLayoutManager(getActivity()));
                iv.setHasFixedSize(true);
        context = inflater.getContext();

                mOrder = new OrderAdapterDelivered(orders);
                iv.setAdapter(mOrder);
                return view;
    }

}
