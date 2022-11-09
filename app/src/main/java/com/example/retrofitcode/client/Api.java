package com.example.retrofitcode.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Api
{
    private static Api instance = null;
    private final ApiInterface myApi;

    private Api()
    {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://app.novagems.io:8012/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        myApi = retrofit.create(ApiInterface.class);
    }
    public static synchronized Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }
    public ApiInterface getMyApi() {
        return myApi;
    }

}
