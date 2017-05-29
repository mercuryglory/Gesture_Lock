package com.demo.gesturelock;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast sToast;

//    private static Context sContext;

    public static void showToast(Context context, String msg) {
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }
        sToast.setText(msg);
        sToast.show();
    }
}