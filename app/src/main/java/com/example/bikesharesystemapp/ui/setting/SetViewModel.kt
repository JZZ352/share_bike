package com.example.bikesharesystemapp.ui.setting

import androidx.lifecycle.ViewModel

object SetViewModel :ViewModel() {
    private var ip = "192.168.111.215"
    private var port = "30000"
    private var bike1 ="Hwdre1008270AC7CAT"//单车1卡号
    //单车1
    fun getBike1():String{
        return bike1
    }
    fun setBike1(bike1:String){
        this.bike1=bike1
    }

    private var bike2 ="Hwdre1008A04EAC1BT"//单车2卡号
    //单车2
    fun getBike2():String{
        return bike2
    }
    fun setBike2(bike2:String){
        this.bike2=bike2
    }

    private var battery ="Hwdre100803DDD79AT"//电池卡号
    //电池
    fun getBattery():String{
        return battery
    }
    fun setBattery(battery:String){
        this.battery=battery
    }

    fun getIp():String{
        return ip
    }

    fun setIp(ip:String){
        this.ip = ip
    }

    fun getPort():String{
        return port
    }

    fun setPort(port:String){
        this.port = port
    }
}