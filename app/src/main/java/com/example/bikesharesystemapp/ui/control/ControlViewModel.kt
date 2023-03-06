package com.example.bikesharesystemapp.ui.control

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object ControlViewModel:ViewModel() {

    //控制设备的编号
    private var el_num = "" //电磁锁编号
    private var Rsnum = "" //电机编号

    //控制设备的状态
    var sl_lamp_state = MutableLiveData<String>("off")  //照明灯状态
    var left_state =MutableLiveData<String>("off")//左转向灯状态
    var right_state = MutableLiveData<String>("off") //右转向灯状态
    //控制设备的获取与设置
    //电磁锁
    fun getElNum():String{
        return el_num
    }
    fun setElNum(elNum:String){
        this.el_num = elNum
    }
    //可调灯
    fun getRsNum():String{
        return Rsnum
    }
    fun setRsNum(rsNum:String){
        this.Rsnum= rsNum
    }

}