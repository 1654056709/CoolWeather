package com.example.john.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.coolweather.R;
import com.example.john.coolweather.app.CoolWeatherApplication;
import com.example.john.coolweather.model.City;
import com.example.john.coolweather.model.CoolWeatherDB;
import com.example.john.coolweather.model.Country;
import com.example.john.coolweather.model.Province;
import com.example.john.coolweather.util.ConstantUtil;
import com.example.john.coolweather.util.HttpCallbackListener;
import com.example.john.coolweather.util.HttpUtil;
import com.example.john.coolweather.util.LogUtil;
import com.example.john.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2016/11/12.
 */

public class ChooseAreaActivity extends Activity {
    private static final String TAG = "ChooseAreaActivity";

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTRY = 2;


    private TextView title_text;
    private ListView list_view;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private CoolWeatherDB coolWeatherDB;

    //进度条对话框
    private ProgressDialog progressDialog;

    //省份列表
    private List<Province> provinces;
    //市列表
    private List<City> cities;
    //县列表
    private List<Country> countries;

    //被选中的省份
    private Province selectProvince;
    //被选中的市
    private City selectCity;

    private int current_level;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //判断当前的这个活动是否是从WeatherActivity中跳转来的
        boolean isFromWeatherActivity = getIntent().getBooleanExtra("is_from_weather_activity", false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.getContext());
        //判断文件中是否有城市被选中且不是从WeatherActivity中跳转来的，如果没有就手动选择某个地
        if (sharedPreferences.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            WeatherActivity.actionStart(this, null);
            //当前活动退出任务栈
            this.finish();
            return;
        }

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_choose_area);
        init();
    }

    /**
     * 跳转到该活动中
     *
     * @param context
     * @param isFromWeatherActivity
     */
    public static void actionStart(Context context, boolean isFromWeatherActivity) {
        Intent intent = new Intent(context, ChooseAreaActivity.class);
        intent.putExtra("is_from_weather_activity", isFromWeatherActivity);
        context.startActivity(intent);
    }

    public void init() {
        title_text = (TextView) findViewById(R.id.title_text);
        list_view = (ListView) findViewById(R.id.list_view);
        coolWeatherDB = CoolWeatherDB.getInstance(this);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (current_level == LEVEL_PROVINCE) {
                    selectProvince = provinces.get(i);
                    LogUtil.d(TAG, "id=" + selectProvince.getId() + "");
                    LogUtil.d(TAG, selectProvince.getProvince_name());
                    queryCities();
                    LogUtil.d(TAG, "end");
                } else if (current_level == LEVEL_CITY) {
                    selectCity = cities.get(i);
                    queryCountyies();
                } else if (current_level == LEVEL_COUNTRY) {
                    //跳转显示该县的具体天气信息页面
                    String country_name = countries.get(i).getCountry_name();
                    WeatherActivity.actionStart(ChooseAreaActivity.this, country_name);
                    ChooseAreaActivity.this.finish();
                }
            }
        });
        //加载省份信息
        queryProvinces();
    }


    /**
     * 加载省份数据
     */
    public void queryProvinces() {
        //先在数据库中查如果查不到的话，再在在网络中查询
        provinces = coolWeatherDB.loadProvinces();
        if (provinces.size() > 0) {
            dataList.clear();
            for (Province province : provinces) {
                dataList.add(province.getProvince_name());
            }
            //适配器通知数据发生改变
            adapter.notifyDataSetChanged();
            //list_view设置默认选中项
            list_view.setSelection(0);
            //更新UI操作
            title_text.setText("中国");
            current_level = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, ConstantUtil.AreaType.PROVINCE);
        }
    }


    /**
     * 查询某个省份下所有的市
     */
    public void queryCities() {
        cities = coolWeatherDB.loadCities(selectProvince.getId());
        if (cities.size() > 0) {
            dataList.clear();
            for (City city : cities) {
                dataList.add(city.getCity_name());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            title_text.setText(selectProvince.getProvince_name());
            LogUtil.d(TAG, "标题：" + title_text.getText().toString());
            title_text.setText(selectProvince.getProvince_name());
            current_level = LEVEL_CITY;
        } else {
            LogUtil.d(TAG, "网络");
            queryFromServer(selectProvince.getProvince_code(), ConstantUtil.AreaType.CITY);
        }
    }

    /**
     * 查询摸个市下所有的县
     */
    public void queryCountyies() {
        countries = coolWeatherDB.loadCounties(selectCity.getId());
        if (countries.size() > 0) {
            dataList.clear();
            for (Country country : countries) {
                dataList.add(country.getCountry_name());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            current_level = LEVEL_COUNTRY;
            title_text.setText(selectCity.getCity_name());
        } else {
            queryFromServer(selectCity.getCity_code(), ConstantUtil.AreaType.COUNTRY);
        }
    }


    /**
     * 传入相应的code和type从服务器上取得数据
     *
     * @param code
     * @param type 请求数据类型
     */
    private void queryFromServer(String code, final String type) {
        String address = null;
        if (!TextUtils.isEmpty(code)) {
            address = ConstantUtil.WeatherApi.BASE_ADDRESS + code + ConstantUtil.WeatherApi.ADDRESS_SUFFIX_XML;
        } else {
            address = ConstantUtil.WeatherApi.BASE_ADDRESS + ConstantUtil.WeatherApi.ADDRESS_SUFFIX_XML;
        }

        //更好的交互-->显示进度对话框
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            private boolean result = false;

            @Override
            public void onSuccess(String data) {
                if (ConstantUtil.AreaType.PROVINCE.equals(type)) {
                    //说明联网所得到的数据为province
                    result = Utility.handleProvincesResponse(coolWeatherDB, data);
                } else if (ConstantUtil.AreaType.CITY.equals(type)) {
                    result = Utility.handleCitiesResponse(coolWeatherDB, data, selectProvince.getId());
                    LogUtil.d(TAG, "数据库更新" + selectProvince.getId());
                } else if (ConstantUtil.AreaType.COUNTRY.equals(type)) {
                    result = Utility.handleCountriesResponse(coolWeatherDB, data, selectCity.getId());
                }

                //为了测试效果 延时3秒
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (result) {
                    //设计到了UI操作，由子线程转到主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭进度条对话框
                            closeProgressDialog();
                            //再次调用就会从数据库中拿出来数据
                            if (ConstantUtil.AreaType.PROVINCE.equals(type)) {
                                queryProvinces();
                            } else if (ConstantUtil.AreaType.CITY.equals(type)) {
                                queryCities();
                                LogUtil.d(TAG, "再次查询");
                            } else if (ConstantUtil.AreaType.COUNTRY.equals(type)) {
                                queryCountyies();
                            }
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度条对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


    /**
     * 关闭进度条对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (current_level == LEVEL_PROVINCE) {
            this.finish();
        } else if (current_level == LEVEL_CITY) {
            queryProvinces();
        } else if (current_level == LEVEL_COUNTRY) {
            queryCities();
        }
    }
}
