package com.example.john.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by John on 2016/11/11.
 */

public class CoolWeathSQLiteOpenHelper extends SQLiteOpenHelper {
    //创建省份表
    private static final String TABLE_PROVINCE = "create table province  (\n" +
            "                id Integer primary key autoincrement ,\n" +
            "                province_name text ,\n" +
            "                province_code text\n" +
            "                );";

    //创建市表
    private static final String TABLE_CITY = " create table city (\n" +
            "                 id Integer primary key autoincrement ,\n" +
            "                 city_name text ,\n" +
            "                 city_code text ,\n" +
            "                 province_id Integer\n" +
            "                 );";

    //创建县表
    private static final String TABLE_COUNTRY = " create table country (\n" +
            "                id Integer primary key autoincrement ,\n" +
            "                country_name text ,\n" +
            "                country_code text ,\n" +
            "                city_id Integer\n" +
            "                );";

    /**
     * 构造函数，用来创建数据库
     *
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public CoolWeathSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 在数据库第一次创建的时候使用
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_PROVINCE);
        sqLiteDatabase.execSQL(TABLE_CITY);
        sqLiteDatabase.execSQL(TABLE_COUNTRY);
    }

    /**
     * 在数据库升级时调用
     *
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     * @return void
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:

            default:
        }

    }
}
