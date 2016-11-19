package com.example.john.coolweather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.john.coolweather.R;
import com.example.john.coolweather.app.CoolWeatherApplication;
import com.example.john.coolweather.service.AutoUpdateService;
import com.example.john.coolweather.util.ConstantUtil;
import com.example.john.coolweather.util.HttpCallbackListener;
import com.example.john.coolweather.util.HttpUtil;
import com.example.john.coolweather.util.LogUtil;
import com.example.john.coolweather.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by John on 2016/11/13.
 * 显示具体天气信息
 */

public class WeatherActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "WeatherActivity";

    /**
     * 天气信息布局
     */
    private LinearLayout weather_info_layout;
    /**
     * 城市名称
     */
    private TextView city_name;

    /**
     * 天气信息发布具体时间
     */
    private TextView publish_text;

    /**
     * 描述天气
     */
    private TextView weather_desc;

    /**
     * 最低气温
     */
    private TextView min_temp;

    /**
     * 最高气温
     */
    private TextView max_temp;


    /**
     * 切换城市
     */
    private Button switch_city;

    /**
     * 手动刷新天气
     */
    private Button refresh_weather;


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_weather);
        init();

    }

    /**
     * 跳转到该活动
     *
     * @param context
     * @param country_code
     */
    public static void actionStart(Context context, String country_code) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(ConstantUtil.TableCountry.COUNTRY_NAME, country_code);
        context.startActivity(intent);
    }

    /**
     * 初始化相关信息
     */
    private void init() {
        //创建RequestQueue对象
        weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
        city_name = (TextView) findViewById(R.id.city_name);
        publish_text = (TextView) findViewById(R.id.publish_text);
        weather_desc = (TextView) findViewById(R.id.weather_desc);
        min_temp = (TextView) findViewById(R.id.min_temp);
        max_temp = (TextView) findViewById(R.id.max_temp);
        switch_city = (Button) findViewById(R.id.switch_city);
        switch_city.setOnClickListener(this);
        refresh_weather = (Button) findViewById(R.id.refresh_weather);
        refresh_weather.setOnClickListener(this);

        //得到县级地区代号
        String country_name = getIntent().getStringExtra(ConstantUtil.TableCountry.COUNTRY_NAME);

        if (!TextUtils.isEmpty(country_name)) {
            //不显示，数据信息没有处理完
            weather_info_layout.setVisibility(View.INVISIBLE);
            city_name.setVisibility(View.INVISIBLE);
            publish_text.setText("同步中...");
            LogUtil.d(TAG, "country_code :" + country_name);
            queryFromServer(country_name);
        } else {
            //城市名为空，直接显示天气信息（已经选中城市的天气信息）
            showWeatherInfo();
        }
    }


    /**
     * 根据城市名来查询天气信息
     *
     * @param country_name 城市名
     */
    public void queryFromServer(String country_name) {
        try {
            country_name = URLEncoder.encode(country_name, "UTF-8");
            LogUtil.d(TAG, "url_code=" + country_name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String address = ConstantUtil.WeatherApi.BASE_WEATHER_INFO_ADDRESS + country_name;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onSuccess(String data) {
                //将获取到的数据写入到shared_pres文件夹下的以当前应用程序的包名的xml文件中
                Utility.handleWeatherResponse(data);
                LogUtil.d(TAG, "onSuccess");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeatherInfo();
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    /**
     * 显示天气信息
     */
    private void showWeatherInfo() {
        //天气信息存在shared_pres目录中，以当前应用程序的包名作为名称
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.getContext());
        //城市名称
        String city_name = sharedPreferences.getString("city_name", "");
        LogUtil.d(TAG, "city_name=" + city_name);
        this.city_name.setText(city_name);
        //气温
        String min_temp = sharedPreferences.getString("min_temp", "");
        String max_temp = sharedPreferences.getString("max_temp", "");
        this.min_temp.setText(min_temp);
        this.max_temp.setText(max_temp);
        //信息描述
        String weather_desc = sharedPreferences.getString("weather_desc", "");
        this.weather_desc.setText(weather_desc);
        //发布时间
        String publish_time = sharedPreferences.getString("publish_time", "");
        this.publish_text.setText("今天" + publish_time + "发布");

        //让隐藏的内容显示出来
        weather_info_layout.setVisibility(View.VISIBLE);
        this.city_name.setVisibility(View.VISIBLE);


        //启动后台自动更新天气xml文件服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        this.startService(intent);
    }


    /**
     * 点击事件响应
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //切换城市
            case R.id.switch_city:
                ChooseAreaActivity.actionStart(this, true);
                this.finish();
                break;

            //刷新天气
            case R.id.refresh_weather:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.getContext());
                String city_name = sharedPreferences.getString("city_name", "");
                this.publish_text.setText("同步中...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queryFromServer(city_name);
                break;
        }

    }
}
