package com.example.john.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.john.coolweather.db.CoolWeathSQLiteOpenHelper;
import com.example.john.coolweather.util.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2016/11/12.
 * 对数据库进行管理
 */

public class CoolWeatherDB {
    //数据库版本
    private static final int VERSION = 1;
    //数据库名称
    private static final String DB_NAME = "cool_weather";
    //单例
    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    private CoolWeatherDB(Context context) {
        CoolWeathSQLiteOpenHelper coolWeathSQLiteOpenHelper = new CoolWeathSQLiteOpenHelper(context, DB_NAME, null, VERSION);
        db = coolWeathSQLiteOpenHelper.getWritableDatabase();
    }

    /**
     * 懒汉式 单例模式  synchronized (避免多线程问题)
     *
     * @param context
     * @return CoolweatherDB
     */
    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /**
     * 保存省份数据
     *
     * @param province
     * @return void
     */
    public void saveProvince(Province province) {
        ContentValues values = new ContentValues();
        values.put(ConstantUtil.TableProvince.PROVINCE_NAME, province.getProvince_name());
        values.put(ConstantUtil.TableProvince.PROVINCE_CODE, province.getProvince_code());
        db.insert(ConstantUtil.TableProvince.TABLE_NAME, null, values);
    }

    /**
     * 读取全国所有省份信息
     *
     * @return List
     */
    public List<Province> loadProvinces() {
        List<Province> provinces = new ArrayList<>();
        Province province = null;
        Cursor cursor = db.query(ConstantUtil.TableProvince.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            province = new Province();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String provice_name = cursor.getString(cursor.getColumnIndex(ConstantUtil.TableProvince.PROVINCE_NAME));
            String provice_code = cursor.getString(cursor.getColumnIndex(ConstantUtil.TableProvince.PROVINCE_CODE));
            province.setProvince_name(provice_name);
            province.setProvince_code(provice_code);
            province.setId(id);

            provinces.add(province);
        }
        return provinces;

    }

    /**
     * 保存市数据
     *
     * @param city
     * @return void
     */
    public void saveCity(City city) {
        ContentValues values = new ContentValues();
        values.put(ConstantUtil.TableCity.CITY_NAME, city.getCity_name());
        values.put(ConstantUtil.TableCity.CITY_CODE, city.getCity_code());
        values.put(ConstantUtil.TableCity.PROVINCE_ID, city.getProvince_id());
        db.insert(ConstantUtil.TableCity.TABLE_NAME, null, values);
    }

    /**
     * 根据province_id查找，所有的市
     *
     * @param province_id
     * @return List
     */
    public List<City> loadCities(Integer province_id) {
        List<City> cities = new ArrayList<>();
        City city = null;
        Cursor cursor = db.query(ConstantUtil.TableCity.TABLE_NAME, null, ConstantUtil.TableCity.PROVINCE_ID + "=?", new String[]{province_id + ""}, null, null, null);
        while (cursor.moveToNext()) {
            city = new City();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String city_name = cursor.getString(cursor.getColumnIndex(ConstantUtil.TableCity.CITY_NAME));
            String city_code = cursor.getString(cursor.getColumnIndex(ConstantUtil.TableCity.CITY_CODE));
            city.setId(id);
            city.setCity_name(city_name);
            city.setCity_code(city_code);
            city.setProvince_id(province_id);

            cities.add(city);
        }
        return cities;
    }


    /**
     * 保存县数据
     *
     * @param country
     * @return void
     */
    public void saveCountry(Country country) {
        ContentValues values = new ContentValues();
        values.put(ConstantUtil.TableCountry.COUNTRY_NAME, country.getCountry_name());
        values.put(ConstantUtil.TableCountry.COUNTRY_CODE, country.getCoutry_code());
        values.put(ConstantUtil.TableCountry.CITY_ID, country.getCity_id());
        db.insert(ConstantUtil.TableCountry.TABLE_NAME, null, values);
    }

    /**
     * 根据city的id找到所有的县
     *
     * @param city_id
     * @return List
     */

    public List<Country> loadCounties(Integer city_id) {
        List<Country> countries = new ArrayList<>();
        Country country = null;
        Cursor cursor = db.query(ConstantUtil.TableCountry.TABLE_NAME, null, ConstantUtil.TableCountry.CITY_ID + "=?", new String[]{city_id + ""}, null, null, null);
        while (cursor.moveToNext()) {
            country = new Country();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String country_name = cursor.getString(cursor.getColumnIndex(ConstantUtil.TableCountry.COUNTRY_NAME));
            String country_code = cursor.getString(cursor.getColumnIndex(ConstantUtil.TableCountry.COUNTRY_CODE));
            country.setId(id);
            country.setCountry_name(country_name);
            country.setCoutry_code(country_code);
            country.setCity_id(city_id);

            countries.add(country);
        }

        return countries;
    }

}
