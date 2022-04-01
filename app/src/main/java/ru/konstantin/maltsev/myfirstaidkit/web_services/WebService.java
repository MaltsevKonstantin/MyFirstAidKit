package ru.konstantin.maltsev.myfirstaidkit.web_services;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.konstantin.maltsev.myfirstaidkit.objects.Medicine;
import ru.konstantin.maltsev.myfirstaidkit.objects.MedicineHolder;
import ru.konstantin.maltsev.myfirstaidkit.utils.Setting;

public class WebService {
    private static final String ACTION = "act";
    private static final String ACTION_GET_TOKEN = "getToken";
    private static final String ACTION_GET_MEDICATION_LIST = "getMedicationList";
    private static final String ACTION_UPDATE_MEDICINE = "updateMedicine";
    //private static final String MEDICINE = "medicine";
    private static final String TOKEN = "token";
    private static final String PASSWORD = "password";

    private static WebService service;
    private static Retrofit retrofit;

    public interface WebApi {

        @FormUrlEncoded
        @POST("exec")
        Call<Token> getToken(@FieldMap() Map<String, String> params);

        @FormUrlEncoded
        @POST("exec")
        Call<MedicineHolder> getMedicationList(@FieldMap() Map<String, String> params);

        @FormUrlEncoded
        @POST("exec")
        Call<MedicineHolder> updateMedicine(@FieldMap() Map<String,String> params);

    }

    private WebService() {
    }

    public static WebService getInstance() {
        if (service == null) service = new WebService();
        return service;
    }

    public void create(Context context) {
        Setting setting = new Setting(context);
        retrofit = new Retrofit.Builder()
                .baseUrl(setting.getServiceUrl())
                .client(new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void getToken(@NonNull String password, @NonNull Callback<Token> callback) {
        Map<String, String> params = new HashMap<>();
        params.put(ACTION, ACTION_GET_TOKEN);
        params.put(PASSWORD, password);
        retrofit.create(WebApi.class).getToken(params).enqueue(callback);
    }

    public void getMedicationList(@NonNull Context context, @NonNull Callback<MedicineHolder> callback) {
        Setting setting = new Setting(context);
        Map<String, String> params = new HashMap<>();
        params.put(ACTION, ACTION_GET_MEDICATION_LIST);
        params.put(TOKEN, setting.getToken());
        retrofit.create(WebApi.class).getMedicationList(params).enqueue(callback);
    }

    public void updateMedicine(@NonNull Context context, @NonNull Medicine medicine, @NonNull Callback<MedicineHolder> callback) {
        Setting setting = new Setting(context);
        Map<String, String> params = new HashMap<>();
        params.put(ACTION, ACTION_UPDATE_MEDICINE);
        params.put(TOKEN, setting.getToken());
        params.put("medicine", medicine.getJsonString());
        retrofit.create(WebApi.class).updateMedicine(params).enqueue(callback);
    }

    public static class Token {
        @SerializedName("code")
        int code;

        @SerializedName("token")
        String token;

        public boolean isOk() {
            return code == 3;
        }

        public String getToken() {
            return token;
        }
    }
}
