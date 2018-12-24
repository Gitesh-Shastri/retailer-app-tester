package com.developer.medicento.retailerappmedi;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.medicento.retailerappmedi.data.Constants;
import com.developer.medicento.retailerappmedi.data.OrderAdapter;
import com.developer.medicento.retailerappmedi.data.OrderAdapterC;
import com.developer.medicento.retailerappmedi.data.OrderAdapterDelivered;
import com.developer.medicento.retailerappmedi.data.RecentOrder;
import com.developer.medicento.retailerappmedi.data.RecentOrderC;
import com.developer.medicento.retailerappmedi.data.RecentOrderDelivered;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CanceledFragment extends Fragment {
    private static String url = "";

    private static OrderAdapterDelivered mOrder;
    RecyclerView iv;
    View view;

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

                mOrder = new OrderAdapterDelivered(orders);
                iv.setAdapter(mOrder);
                return view;
    }

}
