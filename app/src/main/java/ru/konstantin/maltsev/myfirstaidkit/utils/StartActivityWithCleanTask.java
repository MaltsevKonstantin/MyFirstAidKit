package ru.konstantin.maltsev.myfirstaidkit.utils;

import android.content.Context;
import android.content.Intent;

public class StartActivityWithCleanTask {
    public static void start(Context context, Class<?> c) {
        context.startActivity(new Intent(context, c).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
