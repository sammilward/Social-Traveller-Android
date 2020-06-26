package com.sammilward.socialtravelerandroid.ui.helpers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class DisplayOnThread {
    public static void ShowToast(final Context context, final String message) {
        if (context != null && message != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
