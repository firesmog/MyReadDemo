package com.example.mydemo.accessibility;

import android.content.Context;
import android.content.Intent;

import com.example.mydemo.util.AccessibilityLog;

/**
 * Created by popfisher on 2017/7/6.
 */

public class OpenAccessibilitySettingHelper {
    private static final String ACTION = "action";
    private static final String ACTION_START_ACCESSIBILITY_SETTING = "action_start_accessibility_setting";

    public static void jumpToSettingPage(Context context) {
        try {
            AccessibilityLog.printLog("jump tp access");
            Intent intent = new Intent(context,  AccessibilityOpenHelperActivity.class);
            intent.putExtra(ACTION, ACTION_START_ACCESSIBILITY_SETTING);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignore) {
            AccessibilityLog.printLog("error = " + ignore.getMessage());
        }
    }
}
