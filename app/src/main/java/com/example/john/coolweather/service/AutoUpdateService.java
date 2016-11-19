package com.example.john.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.john.coolweather.activity.WeatherActivity;
import com.example.john.coolweather.app.CoolWeatherApplication;
import com.example.john.coolweather.receiver.AutoUpdateReceiver;
import com.example.john.coolweather.util.ActivityCollector;
import com.example.john.coolweather.util.ConstantUtil;
import com.example.john.coolweather.util.HttpCallbackListener;
import com.example.john.coolweather.util.HttpUtil;
import com.example.john.coolweather.util.LogUtil;
import com.example.john.coolweather.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AutoUpdateService extends Service {
    private static final int REQUEST_CODE = 0;
    private Handler handler;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 启动服务的时候，创建自动更新天气（定时任务）
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread() {
            @Override
            public void run() {
                LogUtil.d(WeatherActivity.TAG, "service-update");
                handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AutoUpdateService.this, "天气更新中...", Toast.LENGTH_SHORT).show();
                    }
                });

                updateWeather();
            }

        }.start();

        //创建系统类对象（Alarm定时机制）
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //更新周期
        long updatePeriod = 1000 * 60 * 60 * 8;
        long triggerAtMillis = SystemClock.elapsedRealtime() + updatePeriod;
        //在特定的某个时刻执行的Intent
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        //设置定时
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, operation);
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 自动更新天气
     */
    private void updateWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.getContext());
        String city_name = sharedPreferences.getString("city_name", "");
        try {
            city_name = URLEncoder.encode(city_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String address = ConstantUtil.WeatherApi.BASE_WEATHER_INFO_ADDRESS + city_name;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onSuccess(String data) {
                Utility.handleWeatherResponse(data);
                ActivityCollector.finishAll(false);
                Intent intent = new Intent(AutoUpdateService.this, WeatherActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }
}
