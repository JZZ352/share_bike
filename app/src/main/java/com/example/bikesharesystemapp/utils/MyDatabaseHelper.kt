package com.example.bikesharesystemapp.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDatabaseHelper(val context: Context?, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    //如果数据库中不存在表就创建一个
    //date时间  type传感器类型  date数据值
    private val createSensor = "create table if not exists Sensor (" +
            "id integer primary key autoincrement," +
            "date text," +
            "type text," +
            "data text) "

    private val createBike1 = "create table if not exists Bike1 (" +
            "id integer primary key autoincrement," +
            "bike1 text)"
    private val createBike2 = "create table if not exists Bike2 (" +
            "id integer primary key autoincrement," +
            "bike2 text)"
    private val createBattery = "create table if not exists Battery (" +
            "id integer primary key autoincrement," +
            "battery text)"


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createSensor)
        db?.execSQL(createBike1)
        Log.d("table,bike1", "数据表创建成功")
    }

    //更新数据库
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Sensor")
        db?.execSQL("drop table if exists Bike1")
        onCreate(db)
    }
}