package com.medicento.retailerappmedi.Utils;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IUploadAPI {

    @Multipart
    @POST("upload_video_to_server/")
    Call<com.medicento.retailerappmedi.data.ResponseBody> uploadFile(@Part MultipartBody.Part file);

}
