package com.uit.sensordht.Interface;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {
    @FormUrlEncoded
    @POST("https://server.uitprojects.com/registration")
    Call<String> createAccount(@Field("email") String email, @Field("password") String password, @Field("username_primary") String username);

}
