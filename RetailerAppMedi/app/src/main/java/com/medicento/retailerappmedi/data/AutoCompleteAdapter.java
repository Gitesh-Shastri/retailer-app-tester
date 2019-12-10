package com.medicento.retailerappmedi.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.interfaces.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.medicento.retailerappmedi.Utils.JsonUtils.getDoubleValue;
import static com.medicento.retailerappmedi.Utils.JsonUtils.getIntegerValueFromJsonKey;
import static retrofit2.CallAdapter.*;

public class AutoCompleteAdapter extends ArrayAdapter<Medicine> {

    private List<Medicine> medicineAutos;
    private List<Medicine> tempMedicines;
    private Context context;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private static final String TAG = "AutoCompleteAdap";
    private boolean isLoading = false;
    private SharedPreferences mSharedPreferences;

    public AutoCompleteAdapter(Context context, ArrayList<Medicine> objects) {
        super(context, R.layout.medicine_row, objects);
        this.context = context;
        this.medicineAutos = new ArrayList<>(objects);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Nullable
    @Override
    public Medicine getItem(int position) {
        return medicineAutos.get(position);
    }

    @Override
    public int getCount() {
        return medicineAutos.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Medicine medicineAuto = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.medicine_row, parent, false
            );
        }

        TextView name, company, mrp, scheme, medicine_discount, packing;

        name = convertView.findViewById(R.id.medicine_name_auto);
        company = convertView.findViewById(R.id.company_name_auto);
        mrp = convertView.findViewById(R.id.medicine_mrp_auto);
        medicine_discount = convertView.findViewById(R.id.medicine_discount);
        packing = convertView.findViewById(R.id.packing);


        if (medicineAuto != null) {
            name.setText(medicineAuto.getMedicentoName());
            company.setText("Company : " + medicineAuto.getCompanyName());
            mrp.setText("\u20B9" + medicineAuto.getmPrice() + "");
            if (!medicineAuto.getDiscount().isEmpty()) {
                medicine_discount.setText("" + medicineAuto.getOffer_qty() + "");
            } else {
                medicine_discount.setText("-");
            }
            if (!medicineAuto.getPacking().isEmpty()) {
                packing.setText(medicineAuto.getPacking());
            } else {
                packing.setText(" - ");
            }
        }

        return convertView;
    }


    @Override
    public Filter getFilter() {
        return medicineFilter;
    }

    Filter medicineFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Medicine medicineAuto = (Medicine) resultValue;
            return medicineAuto.getMedicentoName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults results = new FilterResults();
            final List<Medicine> medicineautos1 = new ArrayList<>();

            if (!(constraint == null || constraint.length() == 0)) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                if (tempMedicines == null) {
                    final String json = mSharedPreferences.getString("medicines_saved", null);
                    Type type = new TypeToken<ArrayList<Medicine>>() {
                    }.getType();
                    tempMedicines = new Gson().fromJson(json, type);

                    if (!isLoading && filterPattern.length() > 0) {

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Api.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        Api api = retrofit.create(Api.class);

                        Call<GetMedicineResponse> getMedicineResponseCall = api.getMedicineResponseCall(filterPattern, "0");

                        getMedicineResponseCall.enqueue(new Callback<GetMedicineResponse>() {
                            @Override
                            public void onResponse(Call<GetMedicineResponse> call, Response<GetMedicineResponse> response) {
                                try {
                                    GetMedicineResponse getMedicineResponse = response.body();

                                    for (MedicineResponse medicineResponse : getMedicineResponse.getMedicineResponses()) {
                                        medicineautos1.add(new Medicine(medicineResponse.getItem_name(),
                                                medicineResponse.getManfc_name(),
                                                medicineResponse.getPtr(),
                                                medicineResponse.getId() + "",
                                                medicineResponse.getItem_code(),
                                                medicineResponse.getQty(),
                                                medicineResponse.getPacking(),
                                                medicineResponse.getMrp(),
                                                medicineResponse.getScheme(),
                                                medicineResponse.getDiscount(),
                                                medicineResponse.getOffer_qty()
                                        ));

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                medicineAutos.clear();
                                medicineAutos.addAll(medicineautos1);
                                Collections.sort(medicineAutos, new Comparator<Medicine>() {
                                    @Override
                                    public int compare(Medicine medicine, Medicine t1) {
                                        return medicine.getMedicentoName().compareToIgnoreCase(t1.getMedicentoName());
                                    }
                                });

                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<GetMedicineResponse> call, Throwable t) {
                                medicineAutos.clear();
                                medicineAutos.addAll(medicineautos1);
                                Collections.sort(medicineAutos, new Comparator<Medicine>() {
                                    @Override
                                    public int compare(Medicine medicine, Medicine t1) {
                                        return medicine.getMedicentoName().compareToIgnoreCase(t1.getMedicentoName());
                                    }
                                });

                                notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    for (Medicine medicine : tempMedicines) {
                        if (medicine.getMedicentoName().trim().toLowerCase().startsWith(filterPattern)) {
                            medicineautos1.add(medicine);
                        }
                    }
                    results.values = medicineautos1;
                    results.count = medicineautos1.size();
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                Log.d("gitesh_result", results.count + "");
                medicineAutos.clear();
                medicineAutos.addAll((ArrayList<Medicine>) results.values);
                Collections.sort(medicineAutos, new Comparator<Medicine>() {
                    @Override
                    public int compare(Medicine medicine, Medicine t1) {
                        return medicine.getMedicentoName().compareToIgnoreCase(t1.getMedicentoName());
                    }
                });
            }
            notifyDataSetChanged();
        }

    };
}
