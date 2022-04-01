package ru.konstantin.maltsev.myfirstaidkit.web_services;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ServerPointerService {
    private static final String SERVER_POINTER_URL = "https://script.google.com/macros/s/AKfycbybT0lemG50qD1pOp4Nlzc1Q-dvNADwR2sNk3aIPkehVhl6XKWyZFev6aspBN8KKhJQDw/";

    public interface ServicePointerApi {
        @FormUrlEncoded
        @POST("exec")
        Call<ServerPointerResponse> getServiceUrl(@Field("owner") String owner);
    }

    public static void getApiUrl(Owner owner, Callback<ServerPointerResponse> callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_POINTER_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JSONObject jo = new JSONObject();
        try {
            jo.put("name", owner.name);
            jo.put("password", owner.password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        retrofit.create(ServicePointerApi.class).getServiceUrl(jo.toString()).enqueue(callback);
    }

    public static class Owner {
        String name;
        String password;

        public void setName(String name) {
            this.name = name;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
