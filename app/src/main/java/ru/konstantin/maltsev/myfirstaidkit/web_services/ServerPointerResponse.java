package ru.konstantin.maltsev.myfirstaidkit.web_services;

import com.google.gson.annotations.SerializedName;

public class ServerPointerResponse {
    private static final int FOUND = 1;

    @SerializedName("code")
    int code;

    @SerializedName("url")
    String url;

    public boolean isFound() {
        return code == FOUND;
    }

    public String getUrl() {
        return url;
    }
}
