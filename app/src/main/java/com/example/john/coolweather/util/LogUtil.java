package com.example.john.coolweather.util;

import android.util.Log;

/**
 * Created by John on 2016/11/12.
 */

public class LogUtil {
    private static final int VERBOSE = 0;
    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARN = 3;
    private static final int ERROR = 4;
    private static final int ASSERT = 5;
    private static final int NOTHING = 6;
    private static int CURRENT = 0;


    public static void v(String tag, String msg) {
        if (CURRENT <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (CURRENT <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (CURRENT <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (CURRENT <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (CURRENT <= ERROR) {
            Log.e(tag, msg);
        }
    }

    public static void a(String tag, String msg) {
        if (CURRENT <= ASSERT) {
        }
    }

    /**
     * 是否显示所有log
     *
     * @param flag
     */
    public static void isALLShowLog(boolean flag) {
        if (!flag) {
            CURRENT = NOTHING;
        } else {
            CURRENT = VERBOSE;
        }
    }

    public static void setCURRENT(int CURRENT) {
        LogUtil.CURRENT = CURRENT;
    }
}
