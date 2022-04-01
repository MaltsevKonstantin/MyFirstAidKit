package ru.konstantin.maltsev.myfirstaidkit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class Setting {
    private static final String PREFERENCES_NAME = "setting";
    private static final String SERVICE_URL = "service_url";
    private static final String TOKEN = "token";




    SharedPreferences preferences;

    public Setting(@NonNull Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setServiceUrl(String url) {
        preferences.edit().putString(SERVICE_URL, url).apply();
    }

    public void clearServiceUrl() {
        setServiceUrl(null);
    }

    public String getServiceUrl() {
        String url = preferences.getString(SERVICE_URL, null);
        if (url != null) url = url.replace("exec", "");
        return url;
    }

    public void setToken(String token) {
        preferences.edit().putString(TOKEN, token).apply();
    }

    public void clearToken() {
        setToken(null);
    }

    public String getToken() {
        return preferences.getString(TOKEN, null);
    }
}
