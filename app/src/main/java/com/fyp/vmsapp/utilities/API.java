package com.fyp.vmsapp.utilities;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface API {
    @GET
    Call<Object> get(@Url String url);

    @POST
    Call<Object> post(@Url String url, @QueryMap Map<String, Object> data);

    @Multipart
    @POST
    Call<Object> post(@Url String url, @QueryMap Map<String, Object> data, @Part MultipartBody.Part image);

    @Multipart
    @POST
    Call<Object> postMultipart(@Url String url, @PartMap Map<String, Object> data,
                               @Part MultipartBody.Part image);
}