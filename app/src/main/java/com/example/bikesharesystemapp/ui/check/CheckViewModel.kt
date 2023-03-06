package com.example.bikesharesystemapp.ui.check

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object CheckViewModel:ViewModel() {

    private var gps ="0000" //gps
    private var motorspeed ="00000"  //电机转速
    private var bikeid = ""     //单车卡号
    private var batteryid = ""  //电池卡号
    private var v ="0"           //电压值
    private var i =""           //电压值
    private var t ="0"           //温度
    private var t1=""           //t1的温度
    //判断是否继续执行循环
    var state = MutableLiveData<String>("true")  //温差保存T1的判断
    //判断单车卡号开锁
    private var lockState1=1    //单车一的开锁状态
    fun getLockState1():Int{
        return this.lockState1
    }
    fun setLockState1(lockState1:Int){
        this.lockState1=lockState1
    }
    private var lockState2=1    //单车二的开锁状态
    fun getLockState2():Int{
        return this.lockState2
    }
    fun setLockState2(lockState2:Int){
        this.lockState2=lockState2
    }
    private var tem="0"  //温度
    private var hum="0"  //湿度
    private var illu="0"  //光照度
    private var o2="0"  //氧气
    private var bp="0"  //大气压
    private var co2="0"  //二氧化碳
    private var infrared ="0"//人体红外


    //各折线图
    var isTempLineChartOpen = false
    var isTLineChartOpen = false    //原hum
    var isIlluLineChartOpen = false
    var isVLineChartOpen = false //原Temp

    //常用展示数据的获取与设置

    //电压值
    fun getV():String {
        return this.v
    }
    fun setV(v: String) {
        this.v= v
    }
    //电流值
    fun getI():String {
        return this.i
    }
    fun setI(i: String) {
        this.i= i
    }
    //温度值
    fun getT():String {
        return this.t
    }
    fun setT(t: String) {
        this.t= t
    }
    //t1温度值
    fun getT1():String {
        return this.t1
    }
    fun setT1(t1: String) {
        this.t1= t1
    }
    //单车卡号
    fun getBikeid():String {
        return this.bikeid
    }
    fun setBikeid(bikeid: String) {
        this.bikeid= bikeid
    }
    //电池卡号
    fun getBatteryid():String {
        return this.batteryid
    }
    fun setBatteryid(batteryid: String) {
        this.batteryid= batteryid
    }
    //gps
    fun getGps():String {
        return this.gps
    }
    fun setGps(gps: String) {
        this.gps= gps
    }
    //转速
    fun getMotorspeed():String {
        return this.motorspeed
    }
    fun setMotorspeed( motorspeed:String) {
        this.motorspeed = motorspeed
    }
    //大气压
    fun getBp():String {
        return this.bp
    }
    fun setBp( bp:String) {
        this.bp = bp
    }
    //氧气
    fun getO2():String {
        return this.o2
    }
    fun setO2( o2:String) {
        this.o2 = o2
    }
    //人体红外
    fun getInfrared():String {
        return this.infrared
    }
    fun setInfrared( infrared:String) {
        this.infrared = infrared
    }
    //二氧化碳
    fun getCo2():String {
        return this.co2
    }
    fun setCo2( co2:String) {
        this.co2 = co2
    }
    //温度
    fun getTem():String {
        return this.tem
    }
    fun setTem( tem:String) {
        this.tem = tem
    }

    //光照度
    fun  getIllu():String {
        return this.illu
    }
    fun setIllu( illu:String) {
        this.illu = illu
    }

}