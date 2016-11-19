package com.example.john.coolweather.util;

import android.app.Activity;
import android.os.Process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by John on 2016/11/18.
 */

public class ActivityCollector {
    private static List<Activity> lists = new ArrayList<>();

    public static void addActivity(Activity activity) {
        lists.add(activity);
    }

    public static void removeActivity(Activity activity) {
        lists.remove(activity);
    }

    public static void finishAll(boolean isKillPid) {
        Iterator<Activity> iterator = lists.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        /**
         * 关闭进程
         */
        if (isKillPid) {
            int pid = Process.myPid();
            Process.killProcess(pid);
        }
    }
}
