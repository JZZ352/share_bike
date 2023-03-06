package com.example.bikesharesystemapp.utils

import android.annotation.SuppressLint
import com.example.bikesharesystemapp.MainViewModel
import com.example.bikesharesystemapp.ui.check.CheckViewModel

//Kotlin延时函数导入相应库


//自动控制情况下，温度光照度对设备的影响
class JudgeThreadValue(val mainActivity: com.example.bikesharesystemapp.MainActivity) {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    //发送数据帧类
    val dataSend = DataSendLight(mainActivity)
    fun judgeIllu(){
        //自动控制情况下，光照度阈值
        if (MainViewModel.isSelfControl){
            //两大预警值
            val curIllu = CheckViewModel.getIllu().toFloat() //光照度
            val setIllu = MainViewModel.getITV().toFloat() //光照度阈值
            if(curIllu<=setIllu){
                sendFrame("Hwdie0208lighton1T")
                 }else{
                //关灯
                sendFrame("Hwdie0208lightoffT")
                 }

        }
    }
    //发送任意数据帧
    fun sendFrame(dataFrame:String){
        mainActivity.sendMsg(dataFrame)
    }
}