package com.example.john.coolweather.entity;

/**
 * Created by John on 2016/11/13.
 * 天气信息实体
 */

public class WeatherInfo {
    private String cityName;
    private String minTemp;
    private String maxTemp;
    private String weahterDesc;
    private String publishTime;

    public WeatherInfo(String cityName, String minTemp, String maxTemp, String weahterDesc, String publishTime) {
        this.cityName = cityName;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.weahterDesc = weahterDesc;
        this.publishTime = publishTime;
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getWeahterDesc() {
        return weahterDesc;
    }

    public void setWeahterDesc(String weahterDesc) {
        this.weahterDesc = weahterDesc;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}
