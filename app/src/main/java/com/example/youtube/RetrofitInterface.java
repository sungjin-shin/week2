package com.example.youtube;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @GET("/api/init")
    Call<JsonObject> executeInit();

    @POST("/api/login")
    Call<JsonObject> executeLogin(@Body HashMap<String, String> map);

    @POST("/api/signup")
    Call<Void> executeSignup (@Body HashMap<String, String> map);
}
