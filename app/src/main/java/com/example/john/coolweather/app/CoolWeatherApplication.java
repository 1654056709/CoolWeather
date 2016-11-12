package com.example.john.coolweather.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by John on 2016/11/12.
 */

public class CoolWeatherApplication extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

}
