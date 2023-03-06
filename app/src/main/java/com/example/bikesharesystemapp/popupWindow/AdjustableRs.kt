package com.example.bikesharesystemapp.popupWindow

//Kotlin延时函数导入相应库
import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bikesharesystemapp.R
import com.example.bikesharesystemapp.ui.check.CheckViewModel
import com.example.bikesharesystemapp.ui.database.DBViewModel
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


class AdjustableRs(val mainActivity: com.example.bikesharesystemapp.MainActivity) {

    //可调灯弹窗相关
    private var popupWindow: PopupWindow? = null
    private var now_angle = "000"    //进度条
    private var timerlight: Timer? = null
    private var perial = "" //定时器时间间隔
    private val sensorRs= "转速"

    //可调灯界面
    @SuppressLint("InflateParams")
    fun rsUserInface(orderHeader:String){

        val deviceNo = "07"//获得可调灯的编号

        //创建弹出窗口视图，和可调灯界面布局联系
        val view: View =
            LayoutInflater.from(mainActivity).inflate(R.layout.dialog_rs, null)
        popupWindow = PopupWindow(
            view,
            800,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        //设置弹窗位置
        popupWindow?.showAtLocation(view, Gravity.CENTER, 5, 5)

        //返回按钮点击事件
        val imageView = view.findViewById<View>(R.id.iv_close_rs) as ImageView
        imageView.setOnClickListener {
            if (orderHeader === "Hw") { //wifi控制才发送，zigbee控制不发送
                if (!TextUtils.isEmpty(deviceNo)) {
                    val orderlight: String = orderHeader + "cal" + deviceNo + "08stopctrlT"
                    //发送数据
                    mainActivity.sendMsg(orderlight)
                }
            }
            //弹窗消失
            popupWindow?.dismiss()
            popupWindow = null
        }

        //获取进度条控件
        val light_control = view.findViewById<View>(R.id.rs_control) as SeekBar
        val parseInt: Int = now_angle.toInt() //将数据帧进度百分比文本转化为数字
        light_control.progress = parseInt  //设置进度条百分比
        //获取新的亮度百分比，将进度条更新
        val light_angle = view.findViewById<View>(R.id.rs_angle) as TextView
        light_angle.text = "亮度:$parseInt%"  //显示当前亮度百分比
        //获取显示转速的控件
        val rs=view.findViewById<TextView>(R.id.conn_tv_motorSpeed)
        val gps=view.findViewById<TextView>(R.id.conn_tv_gps)

        val control_perial = view.findViewById<View>(R.id.control_perial) as EditText
        //设置可调灯发送时间间隔
        control_perial.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                perial = s.toString()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        light_control.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                light_angle.text = "亮度:$progress%"
                var angle = "100"
                //改变进度条使文本相应改变
                if (progress.toString().length == 1) {
                    angle = "00$progress"
                } else if (progress.toString().length == 2) {
                    angle = "0$progress"
                }
                now_angle = angle
            }

            //碰到进度条时的方法
            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }
            //下面的是滑动结束时候的进度条的值
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (orderHeader.contains("Hw")) {
                    if (!TextUtils.isEmpty(deviceNo)) {
                        val orderlight = orderHeader + "crs" + "07" + "03" + now_angle + "T"
                        //发送数据
                        Timer().schedule(500){
                            mainActivity.sendMsg(orderlight)
                        }
                    }
                    timerlight?.cancel()
                    timerlight = null

                }
                Log.d("now_angle","now_angle停止的值："+now_angle)
                //发送随机位置到Model中去
                if(now_angle.toInt()>0){
                    val x = (1000..9999).random().toString()
                    CheckViewModel.setGps(x) //保存到Model中
                    Log.d("随机数", "x"+x)
                }
                //转速的值y=150x,并将y的值存入到checkViewModel中去
                val y =(now_angle.toInt()*150).toString()
                val a:String
                if (y.length==3){
                     a=MessageFormat.format("00"+y)
                    CheckViewModel.setMotorspeed(a)
                }else if (y.length==4){
                     a=MessageFormat.format("0"+y)
                    CheckViewModel.setMotorspeed(a)
                }else if(y.length==5){
                     a=y
                    CheckViewModel.setMotorspeed(a)
                }else{
                    a="00000"
                    CheckViewModel.setMotorspeed(a)
                }
                //根据转速发送阈值报警
                //如果转速大于13500，会发送数据帧Hwcsl0402onT
                if (y.toInt()>13500){
                    Timer().schedule(800){
                        sendFrame("Hwcsl0402onT")
                    }
                }else{
                    Timer().schedule(800){
                        sendFrame("Hwcsl0403offT")
                    }
                }
                //获取系统当前时间,(数据库相关)
                val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                val date = Date(System.currentTimeMillis())
                val time = simpleDateFormat.format(date)
                //存入数据库的方法
                val values_v = ContentValues().apply {
                    //组装数据
                    put("date",time)
                    put("type",sensorRs)
                    put("data",CheckViewModel.getMotorspeed()+"rps")
                    Log.d("存入rs","已存入")
                }
                DBViewModel.db?.insert("Sensor",null,values_v)
                //从Model中获取电压的值
                val z=CheckViewModel.getV()
                Log.d("v的值","获取的v值："+z)
                //将接收到的数据整合起来，做成数据帧发送出去
                if (z.length==1){
                    val order=MessageFormat.format("Hwcre0711~"+CheckViewModel.getGps()+"*"+CheckViewModel.getMotorspeed()+"v0.00T")
                    sendFrame(order)
                }else{
                    val order= MessageFormat.format("Hwcre0711~"+CheckViewModel.getGps()+"*"+CheckViewModel.getMotorspeed()+"v"+z+"T")
                    sendFrame(order)
                }
                seekBar.isEnabled = false
                val msgRelift = Message()
                msgRelift.what = 0x117
                msgRelift.obj = seekBar
                mainActivity.handler.sendMessageDelayed(msgRelift, 500)//延迟1秒后发送
            }
        })

        //打开界面时，发送start控制命令
        if (orderHeader === "Hw") { //wifi控制才发送，zigbee控制不发送
            if (!TextUtils.isEmpty(deviceNo)) {
                val orderlight = orderHeader + "cal" + deviceNo + "09startctrlT"
                //发送数据
                mainActivity.sendMsg(orderlight)
            }
        }
        //此处写了如果是Zigbee传输的话，可调灯的打开代码
        if (!orderHeader.contains("Hw")) {   //不包含Hw 表示就是Zigbee 传输的，显示zigbee按钮
            val sendOrderBtn = view.findViewById<View>(R.id.send_rs_order) as Button
            sendOrderBtn.visibility = View.VISIBLE
            sendOrderBtn.setOnClickListener {
                if (!TextUtils.isEmpty(deviceNo)) {
                    val orderlight = orderHeader + "cal" + deviceNo + "06" + now_angle + "endT"
                    //发送数据
                    mainActivity.sendMsg(orderlight)
                }
            }
        }
    }
    //发送任意数据帧
    fun sendFrame(dataFrame:String){
        mainActivity.sendMsg(dataFrame)
    }
}