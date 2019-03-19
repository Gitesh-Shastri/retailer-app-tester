package com.medicento.retailerappmedi;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.data.Area;
import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class ProfileNew extends AppCompatActivity {

    TextInputLayout firstName, lastName, address, email, number, username;

    SalesPerson sp;

    ProgressDialog progressDialog;

    ArrayAdapter<String> stateadapter, cityeadapter;

    String state_name, city_name, area_name;

    ArrayList<Area> areas, tempareas;
    ArrayList<String> states, cities;

    EditText drug, gst, pan;
    AutoCompleteTextView state, city, area;

    String area_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        firstName = findViewById(R.id.first);
        address = findViewById(R.id.address);
        lastName = findViewById(R.id.second);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        username = findViewById(R.id.username);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        area = findViewById(R.id.area);

        drug = findViewById(R.id.drug);
        pan = findViewById(R.id.pan);
        gst = findViewById(R.id.gst);

        Paper.init(this);
        Gson gson = new Gson();


        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {

            sp = gson.fromJson(cache, SalesPerson.class);

        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details");
        progressDialog.show();
        progressDialog.setCancelable(true);

        String url = "https://retailer-app-api.herokuapp.com/user/getProfile?id="+sp.getId();

        Log.d("url_for_detail", url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject user = jsonObject.getJSONObject("user");
                            JSONObject pharma = jsonObject.getJSONObject("Allocated_Pharma");

                            if(user.has("Allocated_Area")) {
                                JSONObject area1 = jsonObject.getJSONObject("Allocated_Area");
                                state_name = area1.getString("area_state");
                                city_name = area1.getString("area_city");
                                area_name = area1.getString("area_name");
                                state.setText(state_name);
                                area.setText(area_name);
                                city.setText(city_name);
                            }
                            if(user.has("first")) {
                                firstName.getEditText().setText(user.getString("first"));
                                lastName.getEditText().setText(user.getString("second"));
                            }
                            drug.setText(pharma.getString("drug_license"));
                            gst.setText(pharma.getString("gst_license"));
                            pan.setText(pharma.getString("pan_card"));
                            address.getEditText().setText(pharma.getString("pharma_address"));
                            email.getEditText().setText(user.getString("useremail"));
                            number.getEditText().setText(pharma.getString("contact"));
                            username.getEditText().setText(user.getString("username"));
                            state.setText("Karnataka");
                            area.setText("Abbur B.O");
                            city.setText("Ramnagar");

                            fetchArea();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("error", e.getMessage()+"");
                            Toast.makeText(ProfileNew.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileNew.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);

    }

    public void fetchArea() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details");
        progressDialog.show();
        progressDialog.setCancelable(true);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        areas = new ArrayList<>();
        states = new ArrayList<>();
        cities = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.AREA_FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject areaOnject = new JSONObject(response);
                            JSONArray areasArray = areaOnject.getJSONArray("areas");
                            for (int i = 0; i < areasArray.length(); i++) {
                                JSONObject jsonObject = areasArray.getJSONObject(i);
                                areas.add(new Area(jsonObject.getString("area_name"),
                                        jsonObject.getString("_id"),
                                        jsonObject.getString("area_state"),
                                        jsonObject.getString("area_city")));
                            }
                            tempareas = new ArrayList<>();
                            tempareas.add(new Area("Area Not Available",
                                    "0",
                                    "none",
                                    "none"));
                            for (Area area : areas) {
                                if (!states.contains(area.getState())) {
                                    states.add(area.getState());
                                }
                                if (area.getState().equals(areas.get(1).getState())) {
                                    if (!cities.contains(area.getCity())) {
                                        cities.add(area.getCity());
                                    }
                                    if (!tempareas.get(0).getName().equals(area.getName())) {
                                        if (cities.get(0).equals(area.getCity())) {
                                            tempareas.add(area);
                                        }
                                    }
                                }
                            }
                            Collections.sort(states, String.CASE_INSENSITIVE_ORDER);

                            stateadapter = new ArrayAdapter<String>(ProfileNew.this, R.layout.spinner_item_states, states);

                            state.setAdapter(stateadapter);

                            state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    cities.clear();
                                    tempareas.clear();
                                    for (Area area : areas) {
                                        if (area.getState().equals(stateadapter.getItem(position).toString())) {
                                            if (!cities.contains(area.getCity())) {
                                                cities.add(area.getCity());
                                            }
                                            if (cities.size() > 0) {
                                                if (cities.get(0).equals(area.getCity())) {
                                                    tempareas.add(area);
                                                }
                                            }
                                        }
                                    }

                                    Collections.sort(cities, String.CASE_INSENSITIVE_ORDER);

                                    cityeadapter = new ArrayAdapter<String>(ProfileNew.this, R.layout.spinner_item_states, cities);

                                    city.setAdapter(cityeadapter);

                                    city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            tempareas.clear();
                                            for (Area area : areas) {
                                                if (area.getCity().equals(cityeadapter.getItem(position))) {
                                                    tempareas.add(area);
                                                }
                                            }


                                            if (!tempareas.isEmpty()) {
                                                Collections.sort(tempareas, new Comparator<Area>() {
                                                    @Override
                                                    public int compare(Area c1, Area c2) {
                                                        //You should ensure that list doesn't contain null values!
                                                        return c1.getName().compareTo(c2.getName());
                                                    }
                                                });
                                            }
                                            tempareas.add(0, new Area("Area Not Available",
                                                    "0",
                                                    "none",
                                                    "none"));

                                            final ArrayAdapter<Area> areaeadapter = new ArrayAdapter<Area>(ProfileNew.this, R.layout.spinner_item_areas, tempareas);

                                            area.setAdapter(areaeadapter);

                                            area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    area_id = areaeadapter.getItem(position).getId();
                                                }
                                            });

                                        }
                                    });

                                    if (!tempareas.isEmpty()) {
                                        Collections.sort(tempareas, new Comparator<Area>() {
                                            @Override
                                            public int compare(Area c1, Area c2) {
                                                //You should ensure that list doesn't contain null values!
                                                return c1.getName().compareTo(c2.getName());
                                            }
                                        });
                                    }

                                    tempareas.add(0, new Area("Area Not Available",
                                            "0",
                                            "none",
                                            "none"));

                                    final ArrayAdapter<Area> areaeadapter = new ArrayAdapter<Area>(ProfileNew.this, R.layout.spinner_item_areas, tempareas);

                                    area.setAdapter(areaeadapter);

                                    area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            area_id = areaeadapter.getItem(position).getId();
                                        }
                                    });


                                }
                            });

                            Collections.sort(cities, String.CASE_INSENSITIVE_ORDER);

                            cityeadapter = new ArrayAdapter<String>(ProfileNew.this, R.layout.spinner_item_states, cities);

                            city.setAdapter(cityeadapter);

                            city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    tempareas.clear();
                                    for (Area area : areas) {
                                        if (area.getCity().equals(cityeadapter.getItem(position))) {
                                            tempareas.add(area);
                                        }
                                    }


                                    if (!tempareas.isEmpty()) {
                                        Collections.sort(tempareas, new Comparator<Area>() {
                                            @Override
                                            public int compare(Area c1, Area c2) {
                                                //You should ensure that list doesn't contain null values!
                                                return c1.getName().compareTo(c2.getName());
                                            }
                                        });
                                    }

                                    tempareas.add(0, new Area("Area Not Available",
                                            "0",
                                            "none",
                                            "none"));

                                    final ArrayAdapter<Area> areaeadapter = new ArrayAdapter<Area>(ProfileNew.this, R.layout.spinner_item_areas, tempareas);

                                    area.setAdapter(areaeadapter);

                                    area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            area_id = areaeadapter.getItem(position).getId();
                                        }
                                    });

                                }
                            });


                            if (!tempareas.isEmpty()) {
                                Collections.sort(tempareas, new Comparator<Area>() {
                                    @Override
                                    public int compare(Area c1, Area c2) {
                                        //You should ensure that list doesn't contain null values!
                                        return c1.getName().compareTo(c2.getName());
                                    }
                                });
                            }
                            tempareas.add(0, new Area("Area Not Available",
                                    "0",
                                    "none",
                                    "none"));

                            final ArrayAdapter<Area> areaeadapter = new ArrayAdapter<Area>(ProfileNew.this, R.layout.spinner_item_areas, tempareas);

                            area.setAdapter(areaeadapter);

                            area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    area_id = areaeadapter.getItem(position).getId();
                                }
                            });

                            state.setText(state_name);
                            area.setText(area_name);
                            city.setText(city_name);
                            progressDialog.dismiss();
                        } catch (
                                JSONException e) {

                            state.setText(state_name);
                            area.setText(area_name);
                            city.setText(city_name);
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        state.setText(state_name);
                        area.setText(area_name);
                        city.setText(city_name);
                        progressDialog.dismiss();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.save) {
            String url = "https://retailer-app-api.herokuapp.com/pharma/updateUserProfile";

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("updated", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.has("message")) {
                                    Toast.makeText(ProfileNew.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ProfileNew.this, "Some Error Occured Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ProfileNew.this, "Some Error Occured Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("id", sp.getId());
                    params.put("phone", number.getEditText().getText().toString());
                    params.put("pan", pan.getText().toString());
                    params.put("username", username.getEditText().getText().toString());
                    params.put("gst", gst.getText().toString());
                    params.put("drug", drug.getText().toString());
                    params.put("email", email.getEditText().getText().toString());
                    params.put("second", lastName.getEditText().getText().toString());
                    params.put("first", firstName.getEditText().getText().toString());
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }

        return super.onOptionsItemSelected(item);
    }
}
