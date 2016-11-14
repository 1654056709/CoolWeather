package com.example.john.coolweather.util;

/**
 * Created by John on 2016/11/12.
 */


/**
 * 管理各种常量
 */
public final class ConstantUtil {

    public static final class TableProvince {
        public static final String TABLE_NAME = "province";
        public static final String PROVINCE_NAME = "province_name";
        public static final String PROVINCE_CODE = "province_code";
    }

    public static final class TableCity {
        public static final String TABLE_NAME = "city";
        public static final String CITY_NAME = "city_name";
        public static final String CITY_CODE = "city_code";
        public static final String PROVINCE_ID = "province_id";
    }


    public static final class TableCountry {
        public static final String TABLE_NAME = "country";
        public static final String COUNTRY_NAME = "country_name";
        public static final String COUNTRY_CODE = "country_code";
        public static final String CITY_ID = "city_id";
    }

    public static final class WeatherApi {
        public static final String BASE_ADDRESS = "http://www.Weather.com.cn/data/list3/city";
        public static final String ADDRESS_SUFFIX_XML = ".xml";
        public static final String BASE_WEATHER_INFO_ADDRESS = "http://wthrcdn.etouch.cn/weather_mini?city=";
        public static final String ADDRESS_SUFFIX_HTML = ".html";
    }

    public static final class AreaType {
        public static final String PROVINCE = "province";
        public static final String CITY = "city";
        public static final String COUNTRY = "country";
    }

}
