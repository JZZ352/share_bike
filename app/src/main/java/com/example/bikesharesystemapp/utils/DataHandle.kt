package com.example.bikesharesystemapp.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import com.example.bikesharesystemapp.R
import com.example.bikesharesystemapp.ui.check.CheckViewModel
import com.example.bikesharesystemapp.ui.control.ControlViewModel
import com.example.bikesharesystemapp.ui.database.DBViewModel
import com.example.bikesharesystemapp.ui.setting.SetViewModel
import com.example.bikesharesystemapp.utils.DHModel
import com.example.bikesharesystemapp.utils.JudgeThreadValue
import java.text.SimpleDateFormat
import java.util.*

//温湿度显示的数据帧
class DataHandle(private val mainActivity: com.example.bikesharesystemapp.MainActivity, val view:View) {


//    //数据展示
      private val SENSOR_HE = "he";//温湿度
      private val SENSOR_IE = "ie";//光照度
      private val SENSOR_RS = "rs13423";//转速和gps位置
      private val SENSOR_RE = "re" ;//自行车卡号和电池卡号
      private val SENSOR_AF = "af";//电压
      private val SENSOR_JS ="js";//计时器
      //控制
      private val CONTROL_EL = "el" //电磁锁
      private val CONTROL_RS = "rs" //电机
      private val CONTROL_LEFT="left"//左转指示灯
      private val CONTROL_RIGHT="righ"//右转指示灯
      private val CONTROL_LIGHT="ligh"//照明灯
//    获取到自行车卡号和电池卡号显示的Textview
      val connTvBikeid=view.findViewById<TextView>(R.id.conn_tv_bikeid)
      val connTvBattery=view.findViewById<TextView>(R.id.conn_tv_batteryid)

      private val sensorIllu ="光照"
      private val sensorV ="电压"
      private val sensorT = "温度"

@SuppressLint("SimpleDateFormat", "SetTextI18n", "UseSwitchCompatOrMaterialCode")
fun handle(recvData: String){
        try {
            val type = recvData.substring(3, 5) //设备类型
            val typeLight =recvData.substring(9,13)//left,righ,ligh
            Log.d("typeLight","typeLight:"+typeLight)
            //获取系统当前时间,(数据库相关)
            val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
            val date = Date(System.currentTimeMillis())
            val time = simpleDateFormat.format(date)
            //判断阈值
            val judgeTv = JudgeThreadValue(mainActivity)
            Log.d("databaseTest",time)
            when {
                //获取电池温度温差
                //Hwdhe0111t+27.1h45.4T
                type.endsWith(SENSOR_HE) -> {
                    val connTvT =view.findViewById<TextView>(R.id.conn_tv_t)
                    Log.d("state温差","state:"+CheckViewModel.state.value)
                    if(CheckViewModel.state.value=="true"){
                        val temp:String =recvData.substring(11,15).toFloat().toString()
                        val t1=temp.toFloat().toString()
                        CheckViewModel.setT1(t1)
                        CheckViewModel.setT(t1)
                        CheckViewModel.state.value="false"
                    } else if (CheckViewModel.state.value=="false"){
                        val temp:String =recvData.substring(11,15).toFloat().toString()
                        val t2=temp.toFloat()
                        val t=(t2-CheckViewModel.getT1().toFloat()).toString()
                        Log.d("t1,t2","t1:"+CheckViewModel.getT1()+"t2:"+t2+"t:"+t)
                        if(t.toFloat()>10){
                            sendFrame("Hwcsl0402onT")
                        }else{
                            sendFrame("Hwdsl0403offT")
                        }
                        CheckViewModel.setT(t)
                        connTvT?.text="$t T"
                        //存入数据库的方法
                        val values_v = ContentValues().apply {
                            //组装数据
                            put("date",time)
                            put("type",sensorT)
                            put("data",t+"T")
                            Log.d("存入t","已存入")
                        }
                        DBViewModel.db?.insert("Sensor",null,values_v)
                    }
                }
                //获取电压,电流数据
                //Hwdaf1006vo1.67T
                type.endsWith(SENSOR_AF) -> {
                    val connTvV =view.findViewById<TextView>(R.id.conn_tv_v)
                    val connTvI =view.findViewById<TextView>(R.id.conn_tv_i)
                    val v:String=recvData.substring(11,15).toFloat().toString()
                    if (v.toFloat()>5 || v.toFloat()<0.3){
                        sendFrame("Hwcsl0402onT")
                    }else{
                        sendFrame("Hwcsl0403offT")
                    }
                    //根据电压值求出电流值存入checkModel中
                    val i =(v.toFloat()*2).toString()
                    Log.d("i的值","i:"+i)
                    CheckViewModel.setI(i)
                    connTvI?.text="$i A"
                    CheckViewModel.setV(v)
                    connTvV?.text="$v V"
                    //存入数据库的方法
                    val values_v = ContentValues().apply {
                        //组装数据
                        put("date",time)
                        put("type",sensorV)
                        put("data",v+"v")
                        Log.d("存入v","已存入")
                    }
                    DBViewModel.db?.insert("Sensor",null,values_v)
                }

                //获取自行车卡号和电池卡号
//                  Hwcre0711~7028*11111v0.00T	    （7028 为位置  11111为转速
//                  0.00为电压
//                    Hwdre1008        T   （中间八位卡号
//                    Hwdre1009righton11T  右转灯开启
//                    Hwdre1009lefton111T 左转灯开启
//                    Hwdre1009lightoff1T 关闭状态
                type.endsWith(SENSOR_RE) -> {
                    when(typeLight){
                        "left"->{
                            val coonSwLeft = view.findViewById<Switch>(R.id.swLeft)
                            val state =recvData.substring(13,15)
                            //判断左转灯当前状态
                            if (state == "on"){
                                ControlViewModel.left_state.value = "on"
                                coonSwLeft.isChecked = true
                            }else {
                                ControlViewModel.left_state.value = "off"
                                coonSwLeft.isChecked = false
                            }
                        }
                        "righ"->{
                            //右转灯当前状态
                            val coonSwRight = view.findViewById<Switch>(R.id.swRight)
                            val state =recvData.substring(14,16)
                            //判断照明灯当前状态
                            if (state == "on"){
                                ControlViewModel.right_state.value = "on"
                                coonSwRight.isChecked = true
                            }else {
                                ControlViewModel.right_state.value = "off"
                                coonSwRight.isChecked = false
                            }
                        }
                        "ligh"->{
                            val coonSwLeft = view.findViewById<Switch>(R.id.swLeft)
                            val coonSwRight = view.findViewById<Switch>(R.id.swRight)
                            ControlViewModel.left_state.value ="off"
                            coonSwLeft.isChecked=false
                            ControlViewModel.right_state.value ="off"
                            coonSwRight.isChecked=false
                        }
                        else ->{
                            Log.d("else,bike1","执行了:"+SetViewModel.getBike1())
                            when(recvData){
                                SetViewModel.getBike1()->{
                                    val connTvBikeid = view.findViewById<TextView>(R.id.conn_tv_bikeid)
                                    val a=CheckViewModel.getLockState1()
                                    Log.d("lockState","a的值："+a)
                                    Log.d("CA刷一次卡","此时Mount应该为0，实际："+DHModel.getBikeMount())
                                    val bikeid :String =recvData.substring(9,17)
                                    CheckViewModel.setBikeid(bikeid) //保存到Model中
                                    connTvBikeid?.text ="$bikeid "
                                    //实现开锁功能并将a的状态切换
                                    bike1(a)
                                }

                                SetViewModel.getBike2()->{
                                    val connTvBikeid = view.findViewById<TextView>(R.id.conn_tv_bikeid)
                                    val a=CheckViewModel.getLockState2()
                                    val bikeid :String =recvData.substring(9,17)
                                    CheckViewModel.setBikeid(bikeid) //保存到Model中
                                    connTvBikeid?.text ="$bikeid "
                                    //实现开锁功能并将a的状态切换
                                    bike2(a)
                                }
                                SetViewModel.getBattery()->{
                                    val batteryid:String=recvData.substring(9,17)
                                    CheckViewModel.setBatteryid(batteryid)
                                    connTvBattery?.text ="$batteryid "
                                    Log.d("电池卡号","电池卡号+"+batteryid)
                                }
                            }
                        }
                    }
                }
                //光照度
                //Hwdie0206000050T
                type.endsWith(SENSOR_IE) -> {
                    //照明灯
                    if(typeLight.endsWith(CONTROL_LIGHT)){
                        val coonSwLamp = view.findViewById<Switch>(R.id.swlamp)
                        val state =recvData.substring(14,16)
                        Log.d("stateTypeLight","stateTypeLight:"+state)
                        //判断照明灯当前状态
                        if (state == "on"){
                            ControlViewModel.sl_lamp_state.value = "on"
                            coonSwLamp.isChecked = true
                        }else {
                            ControlViewModel.sl_lamp_state.value = "off"
                            coonSwLamp.isChecked = false
                        }
                    }else{//识别光照度
                        val illu:String= recvData.substring(9, 15).toInt().toString()
                        val connTvIllu = view.findViewById<TextView>(R.id.conn_tv_illu)
                        CheckViewModel.setIllu(illu)
                        connTvIllu.text = "$illu lux"
                        Log.d("illu","illu"+illu)

                        //看折线图是否打开，光照的
                        if(CheckViewModel.isIlluLineChartOpen){
                            val intent = Intent("com.example.trafficdetection.currentValue")
                            intent.putExtra("value",illu.toFloat())
                            intent.setPackage(mainActivity.packageName)
                            mainActivity.sendBroadcast(intent)
                        }

                        //判断光照是否超过阈值
                        if(CheckViewModel.getIllu()!="0"){
                            judgeTv.judgeIllu()
                        }
//                  //存入数据库
                        val values_illu = ContentValues().apply {
                            //组装数据
                            put("date",time)
                            put("type",sensorIllu)
                            put("data",illu+"lux")
                        }
                        DBViewModel.db?.insert("Sensor",null,values_illu)
                    }

                }

            }
        } catch ( e:Exception) {
            e.printStackTrace()
        }
    }
    //定义一个统计单车数量的函数
    fun bike1(a: Int) {
        val connTvbikemount =view.findViewById<TextView>(R.id.conn_tv_bikemount)
        when(a){
            1->{//奇数次刷卡，单车数量加一
                //刷一次卡，单车数量加一
                CheckViewModel.setLockState1(0)
                val x=(DHModel.getBikeMount().toInt()+1).toString()
                DHModel.setBikeMount(x)
                connTvbikemount?.text="$x 辆"
                Log.d("x","奇数单车数量加一,x:"+x)
            }
            0->{//偶数次刷卡数量减一
                //刷两次单车数量在线数量减1
                CheckViewModel.setLockState1(1)
                val x=(DHModel.getBikeMount().toInt()-1).toString()
                DHModel.setBikeMount(x)
                connTvbikemount?.text="$x 辆"
                Log.d("x","奇数单车数量减一,x:"+x)
            }
        }
    }
    fun bike2(a: Int) {
        val connTvbikemount =view.findViewById<TextView>(R.id.conn_tv_bikemount)
        when(a){
            1->{//奇数次刷卡，单车数量加一
                //刷一次卡，单车数量加一
                CheckViewModel.setLockState2(0)
                val x=(DHModel.getBikeMount().toInt()+1).toString()
                DHModel.setBikeMount(x)
                connTvbikemount?.text="$x 辆"
                Log.d("x","奇数单车数量加一,x:"+x)
            }
            0->{//偶数次刷卡数量减一
                //刷两次单车数量在线数量减1
                CheckViewModel.setLockState2(1)
                val x=(DHModel.getBikeMount().toInt()-1).toString()
                DHModel.setBikeMount(x)
                connTvbikemount?.text="$x 辆"
                Log.d("x","奇数单车数量减一,x:"+x)
            }
        }
    }

    //发送任意数据帧
    fun sendFrame(dataFrame:String){
        mainActivity.sendMsg(dataFrame)
    }

}