package com.example.john.coolweather.model;

/**
 * Created by John on 2016/11/11.
 */

/**
 * 县实体
 */
public class Country {
    private Integer id;
    private String country_name;
    private String country_code;
    private Integer city_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCoutry_code() {
        return country_code;
    }

    public void setCoutry_code(String country_code) {
        this.country_code = country_code;
    }

    public Integer getCity_id() {
        return city_id;
    }

    public void setCity_id(Integer city_id) {
        this.city_id = city_id;
    }
}
