package com.example.bikesharesystemapp.popupWindow

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import com.example.bikesharesystemapp.MainViewModel
import com.example.bikesharesystemapp.R
import com.example.bikesharesystemapp.ui.check.CheckViewModel
import com.example.bikesharesystemapp.utils.JudgeThreadValue
import com.example.bikesharesystemapp.utils.MyApp.Companion.context
import com.example.connectservera.utils.ToastUtils

//Kotlin延时函数导入相应库
//此页面负责写可调灯的出现的弹窗（附带温湿度，光照折线图显示的界面）
//设置阈值的弹窗界面，包括发送阈值的数据帧
class ThreadValueSet(val mainActivity: com.example.bikesharesystemapp.MainActivity) {

    //弹窗相关
    private var popupWindow: PopupWindow? = null
    //阈值设定界面
    @SuppressLint("InflateParams", "CutPasteId")
    fun threadValueSet(){
        //创建弹出窗口视图，和阈值设置界面布局联系
        val view: View =
            LayoutInflater.from(mainActivity).inflate(R.layout.set_thread_value, null)
        popupWindow = PopupWindow(
            view,
            800,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        //设置弹窗位置
        popupWindow?.showAtLocation(view, Gravity.CENTER, 5, 5)

        val illuTV = view.findViewById<EditText>(R.id.illu_threadValue_1)
        //显示当前阈值的值
        illuTV.setText(MainViewModel.getITV())
        //返回按钮点击事件
        val imageView = view.findViewById<View>(R.id.iv_close_light) as ImageView
        imageView.setOnClickListener {
            //弹窗消失
            popupWindow?.dismiss()
            popupWindow = null
        }
        val btnSet = view.findViewById<Button>(R.id.threadValue_btn) //当设置按钮被点击
        btnSet.setOnClickListener {
            //获取设置的各阈值并保存到共享数据集中
            val illu1 = view.findViewById<EditText>(R.id.illu_threadValue_1).text.toString()
            MainViewModel.setITV(illu1)
            //发送光照度阈值的数据帧
            when(illu1.length){
                4 -> sendFrame("Hwcie0210ill+00"+illu1+"T")
                3 -> sendFrame("Hwcie0210ill+000"+illu1+"T")
                2 -> sendFrame("Hwcie0210ill+0000"+illu1+"T")
            }
        }
            MainViewModel.isSelfControl = true
            //立即判断当前是否超过阈值
            val judgeTv = JudgeThreadValue(mainActivity)
            if(CheckViewModel.getIllu()!="0"){
                judgeTv.judgeIllu()
            }
            ToastUtils.showToast(context,"阈值设置成功！")
        }
    //发送任意数据帧
    fun sendFrame(dataFrame:String){
        mainActivity.sendMsg(dataFrame)
    }
}
