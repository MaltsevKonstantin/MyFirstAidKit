package ru.konstantin.maltsev.myfirstaidkit.objects;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Medicine implements Serializable {
    @SerializedName("id")
    int id = 1;

    @SerializedName("manufacture")
    String manufactureName = "";

    @SerializedName("name")
    String name = "";

    @SerializedName("manufactureDate")
    String manufactureDate = "";

    @SerializedName("expirationDate")
    String expirationDate = "";

    @SerializedName("quantity")
    int quantity = 0;

    @SerializedName("hide")
    boolean hide = false;

    Calendar manufactureCalendar = Calendar.getInstance();
    Calendar expirationCalendar = Calendar.getInstance();

    boolean isWarning = false;
    boolean isDanger = false;

    public void updateCalendars() {
        if (manufactureDate.length() > 0) {
            String date = manufactureDate.replace("d", "");
            String[] dateArray = date.split("\\.");
            manufactureCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]));
            manufactureCalendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1);
            manufactureCalendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]));
        } else {
            manufactureDate = "d" + new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(manufactureCalendar.getTime());
        }

        if (expirationDate.length() > 0) {
            String date = expirationDate.replace("d", "");
            String[] dateArray = date.split("\\.");
            expirationCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]));
            expirationCalendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1);
            expirationCalendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]));
        } else {
            expirationDate = "d" + new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(expirationCalendar.getTime());
        }
    }

    public int getId() {
        return id;
    }

    public String getManufactureName() {
        return manufactureName;
    }

    public void setManufactureName(String manufactureName) {
        this.manufactureName = manufactureName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufactureDate() {
        return manufactureDate.replace("d", "");
    }

    public String getExpirationDate() {
        return expirationDate.replace("d", "");
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public Calendar getManufactureCalendar() {
        return manufactureCalendar;
    }

    public Calendar getExpirationCalendar() {
        return expirationCalendar;
    }

    public boolean isExpirationDateHasExpired() {
        return Calendar.getInstance().compareTo(expirationCalendar) > 0;
    }

    public boolean isWarning() {
        return isWarning;
    }

    public boolean isDanger() {
        return isDanger;
    }

    public String getStringRemainingShelfLife() {
        String remainingShelfLife = "Оставшийся срок годности: ";
        Calendar calendar = Calendar.getInstance();

        long remainingMillisecond = (expirationCalendar.getTimeInMillis() - calendar.getTimeInMillis()) / 1000;
        //2592000 = 30 days
        if (remainingMillisecond < 2592000L) {
            //86400 = 1 day
            int day = ((int) remainingMillisecond / 86400);
            if (day == 1 || day == 21 || day == 31) remainingShelfLife += day + " день";
            else if (day > 1 && day < 5 || day > 21 && day < 25) remainingShelfLife += day + " дня";
            else remainingShelfLife += day + " дней";
            isDanger = true;
        } else {
            int year = expirationCalendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            int month = expirationCalendar.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
            if (month < 0) {
                year--;
                month += 12;
            }
            if (year > 0) {
                if (year < 2) remainingShelfLife += year + " год ";
                else if (year < 5) remainingShelfLife += year + " года ";
                else remainingShelfLife += year + " лет ";
            }
            if (month > 0) {
                if (month < 2) remainingShelfLife += month + " месяц ";
                else if (month < 5) remainingShelfLife += month + " месяца ";
                else remainingShelfLife += month + " месяцев ";
            }
            if (year == 0 && month < 4) isWarning = true;
        }
        return remainingShelfLife;
    }

    public void setManufactureCalendar(int year, int month, int dayOfMonth) {
        manufactureCalendar.set(Calendar.YEAR, year);
        manufactureCalendar.set(Calendar.MONTH, month);
        manufactureCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        manufactureDate = "d" + new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(manufactureCalendar.getTime());
    }

    public void setExpirationCalendar(int year, int month, int dayOfMonth) {
        expirationCalendar.set(Calendar.YEAR, year);
        expirationCalendar.set(Calendar.MONTH, month);
        expirationCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        expirationDate = "d" + new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(expirationCalendar.getTime());
    }

    public int getDeferenceDay() {
        return (int) ((expirationCalendar.getTimeInMillis() - manufactureCalendar.getTimeInMillis()) / 86400000);
    }

    public int getRemainingDay() {
        return (int) ((expirationCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) /86400000);
    }

    public String getJsonString() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("id", id);
            jo.put("manufacture", manufactureName);
            jo.put("name", name);
            jo.put("manufactureDate", manufactureDate);
            jo.put("expirationDate", expirationDate);
            jo.put("quantity", quantity);
            jo.put("hide", hide);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo.toString();
    }
}
