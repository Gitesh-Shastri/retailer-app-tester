package com.medicento.retailerappmedi;

import com.medicento.retailerappmedi.data.GetMedicineResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "http://54.161.199.63:8080/";

    @GET("distributor/get_distributor_medicines_sales/")
    Call<GetMedicineResponse> getMedicineResponseCall();
}
