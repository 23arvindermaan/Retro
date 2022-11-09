package com.example.retrofitcode.client;

import com.example.retrofitcode.model.DataModal;
import com.example.retrofitcode.model.Imagedata;
import com.example.retrofitcode.model.Serial;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface
{
    @GET("/users/list") Call<String> getdatas();

    @GET("/users/list") Call<Serial> getdatass();

    @GET("/group/{id}/users") Call<String> groupList(@Path("id") int groupId);

    @FormUrlEncoded
    @POST("user/edit")
    Call<String> updateUser(@Field("first_name") String first, @Field("last_name") String last);

    @POST("user/edit")
    Call<DataModal> updateUser(@Body DataModal dataModal);

    @Multipart
    @POST("/api/common/upload")
    Call<Imagedata> uploadImage(@Part("title") RequestBody title, @Part MultipartBody.Part file);

}
