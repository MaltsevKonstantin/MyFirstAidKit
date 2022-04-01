package ru.konstantin.maltsev.myfirstaidkit.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import ru.konstantin.maltsev.myfirstaidkit.R;

public class InfoMessage {

    public static void createGood(View anchor, String message) {
        int color = anchor.getContext().getResources().getColor(R.color.good);
        Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(color).show();
    }

    public static void createBad(View anchor, String message) {
        int color = anchor.getContext().getResources().getColor(R.color.danger);
        Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(color).show();
    }

    public static void createWarning(View anchor, String message) {
        int color = anchor.getContext().getResources().getColor(R.color.warning);
        Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(color).show();
    }
}
