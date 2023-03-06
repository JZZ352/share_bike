package com.example.bikesharesystemapp.ui.control

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bikesharesystemapp.MainViewModel
import com.example.bikesharesystemapp.R
import com.example.bikesharesystemapp.databinding.ControlFragmentBinding
import com.example.bikesharesystemapp.popupWindow.AdjustableRs
import com.example.bikesharesystemapp.popupWindow.ThreadValueSet
import com.example.bikesharesystemapp.util.DataHandle
import com.example.bikesharesystemapp.utils.DataSendLight
import com.example.bikesharesystemapp.utils.MyApp
import com.example.connectservera.utils.ToastUtils
import kotlinx.android.synthetic.main.control_fragment.*


class ControlFragment : Fragment() {

    private var _binding: ControlFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity : com.example.bikesharesystemapp.MainActivity
    private var running = false
    private var paused = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private val handler = Handler()

    //定时器相关
    private lateinit var mHandler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ControlFragmentBinding.inflate(inflater,container,false)

        //  初始化mainActivity，需要和mainActivity交互
        mainActivity = activity as com.example.bikesharesystemapp.MainActivity
        //初始化界面状态
        initView()
        return binding.root
    }

    //初始化视图
    @SuppressLint("SetTextI18n")
    fun initView(){
        binding.tvLock.text = ControlViewModel.getElNum()
        binding.tvLight.text = ControlViewModel.getRsNum()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mHandler = Handler(Looper.getMainLooper())

        //数据帧处理类
        val dataHandle = DataHandle(mainActivity,requireView())

        //发送数据帧类
        val dataSendLight =DataSendLight(mainActivity)

        //观察接收数据的变化，变化了就执行相应方法
        MainViewModel.receDate.observe(viewLifecycleOwner, Observer {

            //按理说这个oldReceDate完全是多余的，这个只有数据变化了才会回调此处，但不晓得为啥，
            // 每次切换页面后都会默认执行此处，且用的是之前的receDate数据，此处先留思考后面再细思原因
                dataHandle.handle(it)
                MainViewModel.oldReceDate = it
        })
        //开启左转向灯
        binding.swLeft.setOnClickListener {
//          dataHandle.handle("Hwdre1009lefton111T")
            val status =ControlViewModel.left_state.value
            val type="left"
            if (status!=null){
                dataSendLight.sendCtrlCmdLight(status,type)
            }

        }
        //开启右转向灯
        binding.swRight.setOnClickListener {
            val status =ControlViewModel.right_state.value
            val type="righ"
            if (status!=null){
                dataSendLight.sendCtrlCmdLight(status,type)
            }
        }
        //开启照明灯
        binding.swlamp.setOnClickListener {
            val status=ControlViewModel.sl_lamp_state.value
            val type ="ligh"
            //发送相应数据帧
            if (status!=null){
                dataSendLight.sendCtrlCmdLight(status,type)
            }
        }
        //电磁锁开锁
        binding.btnStart.setOnClickListener {
            sendFrame("Hwche0102onT")
            startTimer()
            ToastUtils.showToast(MyApp.context, "车辆已开锁")
        }
        //电磁锁关闭（也是临时停车）
        binding.btnPause.setOnClickListener {
            sendFrame("Hwche0103offT")
            pauseTimer()
            ToastUtils.showToast(MyApp.context, "车辆临时停车中")
        }
        //直流电机转速点击控制
        binding.btnLight.setOnClickListener {
            AdjustableRs(mainActivity).rsUserInface("Hw")
        }
        //开启阈值设定界面
        binding.selfControlOpen.setOnClickListener {
            ThreadValueSet(mainActivity).threadValueSet()

        }
        //一键停车，并且结束计时
        binding.selfControlClose.setOnClickListener {
            //结束计时器
            stopTimer()

            val coonSwLamp = view?.findViewById<Switch>(R.id.swlamp)
            MainViewModel.isSelfControl = false
            sendFrame("Hwdie0208lightoffT")
            sendFrame("Hwche0103offT")
            //只是存了一个状态，还没有改变
            ControlViewModel.sl_lamp_state.value="off"
            //这里把照明灯的状态改变了
            coonSwLamp?.isChecked = false
            ToastUtils.showToast(context,"已停车，请及时缴费")
        }
    }
    //定时器的开始函数
    private fun startTimer() {
        if (!running) {
            startTime = SystemClock.elapsedRealtime() - elapsedTime
            handler.postDelayed(timerRunnable, 0)
            running = true
        }
    }
    //定时器的暂停函数
    private fun pauseTimer() {
        if (running) {
            handler.removeCallbacks(timerRunnable)
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            paused = true
            running = false
        }
    }
    //定时器的结束函数
    private fun stopTimer() {
        if (running || paused) {
            handler.removeCallbacks(timerRunnable)
            elapsedTime = 0
            paused = false
            running = false
            updateTimerText(elapsedTime)
        }
    }
    //主线程中更新UI的函数
    private val timerRunnable = object : Runnable {
        override fun run() {
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            updateTimerText(elapsedTime)
            handler.postDelayed(this, 1000)
        }
    }
    //更新时间的函数
    private fun updateTimerText(time: Long) {
        val seconds = (time / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val timeString = String.format("%02d:%02d", minutes, remainingSeconds)
        timer_text.text = timeString
    }
        //发送任意数据帧
        fun sendFrame(dataFrame:String){
            mainActivity.sendMsg(dataFrame)
        }
}


