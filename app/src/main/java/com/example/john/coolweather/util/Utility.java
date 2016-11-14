package com.example.john.coolweather.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.renderscript.Script;
import android.text.TextUtils;

import com.example.john.coolweather.activity.WeatherActivity;
import com.example.john.coolweather.app.CoolWeatherApplication;
import com.example.john.coolweather.entity.WeatherInfo;
import com.example.john.coolweather.model.City;
import com.example.john.coolweather.model.CoolWeatherDB;
import com.example.john.coolweather.model.Country;
import com.example.john.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by John on 2016/11/12.
 * 网络请求的省，市，县的相关数据处理-----保存到数据库中
 */

public class Utility {
    /**
     * 处理从网络请求到的所有省份的信息，保存到本体数据库
     *
     * @param coolWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        boolean flag = false;
        Province temp = null;
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (int i = 0; i < allProvinces.length; i++) {
                    temp = new Province();
                    String[] array = allProvinces[i].split("\\|");
                    temp.setProvince_code(array[0]);
                    temp.setProvince_name(array[1]);
                    //将得到的信息保存到数据库
                    coolWeatherDB.saveProvince(temp);
                }
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 处理返回的所有的市相关信息，保存到数据库中
     *
     * @param coolWeatherDB
     * @param response
     * @param province_id
     * @return
     */

    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, Integer province_id) {
        boolean flag = false;
        City temp = null;
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String city : allCities) {
                    temp = new City();
                    String[] array = city.split("\\|");
                    temp.setCity_code(array[0]);
                    temp.setCity_name(array[1]);
                    temp.setProvince_id(province_id);
                    coolWeatherDB.saveCity(temp);
                }
                flag = true;
            }

        }
        return flag;
    }


    /**
     * 处理网络请求返回的县相关数据
     *
     * @param coolWeatherDB
     * @param response
     * @param city_id
     * @return
     */
    public synchronized static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB, String response, Integer city_id) {
        boolean flag = false;
        Country temp = null;
        if (!TextUtils.isEmpty(response)) {
            String[] allCountries = response.split(",");
            if (allCountries != null && allCountries.length > 0) {
                for (String country : allCountries) {
                    temp = new Country();
                    String[] array = country.split("\\|");
                    temp.setCoutry_code(array[0]);
                    temp.setCountry_name(array[1]);
                    temp.setCity_id(city_id);
                    //保存到数据库中
                    coolWeatherDB.saveCountry(temp);
                }
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 解析服务器返回的JSON数据
     */
    public synchronized static void handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfoJsonObject = jsonObject.getJSONObject("data");
            String city_name = weatherInfoJsonObject.getString("city");
            JSONArray jsonArray = weatherInfoJsonObject.getJSONArray("forecast");
            JSONObject todayJsonObject = jsonArray.getJSONObject(0);
            String min_temp = todayJsonObject.getString("low");


            String max_temp = todayJsonObject.getString("high");
            LogUtil.d(WeatherActivity.TAG, "max_temp=" + max_temp);
            String weather_desc = todayJsonObject.getString("type");
            String publish_time = todayJsonObject.getString("date");

            WeatherInfo weatherInfo = new WeatherInfo(city_name, min_temp, max_temp, weather_desc, publish_time);
            LogUtil.d(WeatherActivity.TAG, "weatherInfo_city_name=" + weatherInfo.getCityName());
            saveWeatherInfo(weatherInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将天气信息放在SharedPreferences中(持久化操作)
     */
    public static void saveWeatherInfo(WeatherInfo weatherInfo) {
        LogUtil.d(WeatherActivity.TAG, "start");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.getContext());
        LogUtil.d(WeatherActivity.TAG, "end");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city_name", weatherInfo.getCityName());
        editor.putString("min_temp", weatherInfo.getMinTemp());
        editor.putString("max_temp", weatherInfo.getMaxTemp());
        editor.putString("weather_desc", weatherInfo.getWeahterDesc());
        editor.putString("publish_time", weatherInfo.getPublishTime());
        editor.commit();

    }
}
