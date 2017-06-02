package com.davego44.familysync.helper;

import android.content.Context;
import android.widget.LinearLayout;

import com.davego44.familysync.R;

/**
 * Created by daveg on 5/25/2017.
 */

/**
 * Handles generic utilities
 */
public class Utilities {
    public static String encodeEmail(String email) {
        return email.replace(".", ",");
    }
    public static String decodeEmail(String email) { return email.replace(",", "."); }
    public static LinearLayout getColorBox(Context context) {
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(25, 25);
        params.setMargins(2, 2, 2, 2);
        layout.setLayoutParams(params);
        layout.setBackgroundResource(R.color.myProfileColor);
        return layout;
    }
}
