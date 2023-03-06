package com.example.bikesharesystemapp.utils

import android.annotation.SuppressLint
import com.example.bikesharesystemapp.ui.control.ControlViewModel
import com.example.connectservera.utils.ToastUtils

class DataSendLight(val mainActivity: com.example.bikesharesystemapp.MainActivity) {
    private val CONTROL_LEFT ="left"
    private val CONTROL_RIGHT ="righ"
    private val CONTROL_LIGHT ="ligh"
    /***
     *
     * 发送命令
     *
     * **/
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun sendCtrlCmdLight( status:String, type:String) {
        when(type){
            CONTROL_LEFT ->{
                if (status=="on"){
                    sendFrame("Hwdre1009lightoff1T")
                    ControlViewModel.left_state.value="off"
                    ToastUtils.showToast(MyApp.context, "转向灯关闭指令已发送")
                }else{//status=="off"
                    sendFrame("Hwdre1009lefton111T")
                    ControlViewModel.left_state.value="on"
                    ToastUtils.showToast(MyApp.context,"开启左转向灯")
                }

            }
            CONTROL_RIGHT ->{
                if (status=="on"){
                    sendFrame("Hwdre1009lightoff1T")
                    ControlViewModel.right_state.value="off"
                    ToastUtils.showToast(MyApp.context, "转向灯关闭指令已发送")
                }else{//status=="off"
                    sendFrame("Hwdre1009righton11T")
                    ControlViewModel.right_state.value="on"
                    ToastUtils.showToast(MyApp.context,"开启右转向灯")
                }
            }
            CONTROL_LIGHT ->{
                if (status=="on"){
                    sendFrame("Hwdie0208lightoffT")
                    ControlViewModel.sl_lamp_state.value="off"
                    ToastUtils.showToast(MyApp.context, "关闭照明灯")
                }else{//status=="off"
                    sendFrame("Hwdie0208lighton1T")
                    ControlViewModel.sl_lamp_state.value="on"
                    ToastUtils.showToast(MyApp.context,"开启照明灯")
                }
            }
        }
    }
    //发送任意数据帧
    fun sendFrame(dataFrame:String){
        mainActivity.sendMsg(dataFrame)
    }
}