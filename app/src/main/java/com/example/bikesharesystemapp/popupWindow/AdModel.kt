package com.example.bikesharesystemapp.popupWindow

import androidx.lifecycle.MutableLiveData
import java.util.*

object AdModel {
    var parseInt = 0
    //转速
    private var a="00000"

    fun getA():String {
        return this.a
    }
    fun setA(a: String) {
        this.a= a
    }
    //是否发送随机位置
    private var locationState =0

    fun getLocationState():Int{
        return this.locationState
    }
    fun setLocationState(locationState:Int){
        this.locationState=locationState
    }
    var now_angle = MutableLiveData("000")//直流电机当前进度
    var positionTimer: Timer? = null
    var isChange = true  //是否要改变
    //定时器状态
    var Timer_state = MutableLiveData<String>("true") //电磁锁状态


    //获取与设置的方法
    @JvmName("getParseInt1")
    fun getParseInt():Int {
        return this.parseInt
    }
    @JvmName("setParseInt1")
    fun setParseInt(parseInt: Int) {
        this.parseInt= parseInt
    }

}