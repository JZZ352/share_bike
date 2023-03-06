package com.example.bikesharesystemapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//数据共享处，设置为单一类
object MainViewModel:ViewModel() {

    //接收的数据帧数据
    var receDate = MutableLiveData<String>("")
    //用于做对比，防止默认执行观察
    var oldReceDate = ""
    //是否开启自动控制
    var isSelfControl = false

    //阈值状态，是否超过
    var illu_state_former = "1"
    var illu_state_after = ""

    //预警阈值
    private var temp_threadValue1 = "0"
    private var temp_threadValue2 = "0"
    private var illu_threadValue = "0"
    private var v_low_threadValue ="0"
    private var v_high_threadValue ="0"
    private var rs_threadValue ="0"


    //获取与设置阈值
    //光照度阈值
    fun getITV(): String {
        return this.illu_threadValue
    }

    fun setITV(t:String){
        this.illu_threadValue = t
    }
    //电压下限阈值
    fun getV_low_thread(): String {
        return this.v_low_threadValue
    }

    fun setV_low_thread(t:String){
        this.v_low_threadValue = t
    }
    //电压上限阈值
    fun getV_high_thread(): String {
        return this.v_high_threadValue
    }

    fun setV_high_thread(t:String){
        this.v_high_threadValue = t
    }
    //转速阈值
    fun getRs_thread(): String {
        return this.rs_threadValue
    }

    fun setRs_thread(t:String){
        this.rs_threadValue = t
    }


    fun setReceDate(Date:String){
        receDate.value=Date
    }
}