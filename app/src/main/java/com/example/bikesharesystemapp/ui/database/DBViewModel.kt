package com.example.bikesharesystemapp.ui.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.ViewModel
import com.example.bikesharesystemapp.entity.SensorData

object DBViewModel:ViewModel() {

    //选择的日期
    var time = ""

    //传感器类型
    var type = ""

    //查询的数据库结果
    var cursor:Cursor? = null

    val sensorList = ArrayList<SensorData>()

    internal var db: SQLiteDatabase? = null
}