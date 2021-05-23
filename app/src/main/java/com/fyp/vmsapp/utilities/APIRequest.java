package com.fyp.vmsapp.utilities;

import android.accounts.NetworkErrorException;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.ConnectionSpec;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRequest {
    public static void request(final String authorization, final int method, final String endpoint,
                               final Map data, final MultipartBody.Part file, MultipartBody.Part audio_file,
                               final ResponseInterface responseInterface) {
        String endpoint_url = Constants.EndpointPrefix + endpoint;

        final String content_type;

        if (method == Constants.MethodPOSTMultipart) {
            content_type = "multipart/form-data";
        } else {
            content_type = "application/x-www-form-urlencoded";
        }

        OkHttpClient client;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.COMPATIBLE_TLS,
                        ConnectionSpec.CLEARTEXT))
                .connectTimeout(Constants.RequestTimeoutDuration, Constants.RequestTimeoutTimeUnit)
                .callTimeout(Constants.RequestTimeoutDuration, Constants.RequestTimeoutTimeUnit)
                .readTimeout(Constants.RequestTimeoutDuration, Constants.RequestTimeoutTimeUnit)
                .writeTimeout(Constants.RequestTimeoutDuration, Constants.RequestTimeoutTimeUnit);

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .addHeader("Authorization", authorization)
                    .header("Content-Type", content_type)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        });

        client = httpClient.build();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constants.BaseURL)
                .addConverterFactory(GsonConverterFactory.create()).client(client);

        Retrofit retrofit = builder.build();
        API api = retrofit.create(API.class);

        Call<Object> object;

        if (method == Constants.MethodGET) {
            if (!data.isEmpty()) {
                endpoint_url += data;
            }

            object = api.get(endpoint_url);
        } else if (method == Constants.MethodPOSTSimple) {
            if ((endpoint.equals(Constants.EndpointAddMember) || endpoint.equals(Constants.EndpointSignup)) && file != null){
                object = api.post(endpoint_url, data, file);
            } else {
                object = api.post(endpoint_url, data);
            }
        } else {
            object = api.postMultipart(endpoint_url, data, file);
        }

        object.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NotNull Call<Object> call,
                                   @NotNull retrofit2.Response<Object> response) {
                if (response.body() != null) {
                    Gson gson = new Gson();

                    String json = gson.toJson(response.body());

                    try {
                        JSONObject json_object = new JSONObject(json);

                        if (json_object.getDouble("status") == 0d) {
                            responseInterface.response(json_object);
                        } else {
                            String message = json_object.getString("message");

                            if (json_object.has("errors")) {
                                message += " " + json_object.getString("errors");
                            }

                            responseInterface.failure(message);
                        }
                    } catch (JSONException e) {
                        String message = "Request Error: " + e.getMessage();

                        responseInterface.failure(message);
                    }
                } else {
                    String message = "No Response";

                    responseInterface.failure(message);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Object> call, @NotNull Throwable throwable) {
                String message;
                if (throwable instanceof ConnectException || throwable instanceof NetworkErrorException || throwable instanceof SSLHandshakeException) {
                    message = "No Internet Connection";
                } else if (throwable instanceof TimeoutException) {
                    message = "Timeout. Try again";

                } else {
                    message = "Failure: " + throwable.getCause();
                }
                responseInterface.failure(message);
            }
        });
    }
}

