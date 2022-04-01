package ru.konstantin.maltsev.myfirstaidkit.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MedicineHolder {
    public static int CODE_ERROR = 0;
    //public static int CODE_LOADED = 1;
    //public static int CODE_UPDATED = 2;

    @SerializedName("code")
    int code;

    @SerializedName("medicationList")
    List<Medicine> medicationList;

    public boolean isError () {
        return code == CODE_ERROR;
    }

    public List<Medicine> getMedicationList() {
        return medicationList;
    }
}
