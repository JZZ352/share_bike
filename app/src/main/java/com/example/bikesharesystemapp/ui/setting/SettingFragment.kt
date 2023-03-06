package com.example.bikesharesystemapp.ui.setting

//import com.example.farmingsystemapp.MainViewModel
import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bikesharesystemapp.databinding.SettingFragmentBinding
import com.example.bikesharesystemapp.ui.database.DBViewModel
import com.example.connectservera.utils.ToastUtils
import java.util.regex.Matcher
import java.util.regex.Pattern

class SettingFragment : Fragment() {

    private var _binding: SettingFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingFragmentBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.connBtn.setOnClickListener {
            val mainActivity = activity as com.example.bikesharesystemapp.MainActivity

            //服务器IP
            val ip = binding.connEtIp.text.toString().trim()
            //保存到SetViewModel中
            SetViewModel.setIp(ip)
            if (!checkIP(ip)) {
                ToastUtils.showToast(context, "请输入正确的服务器IP")
            }
            //端口号
            val port = binding.connEtPort.text.toString().trim()
            SetViewModel.setPort(port)

            if (TextUtils.isEmpty(port)) {
                ToastUtils.showToast(context, "请输入正确的端口号")
            }
            mainActivity.connectA53Server(ip,port)

        }
    }

    //每次进入界面就会初始化
    fun initView() {
        binding.connEtIp.setText(SetViewModel.getIp())
        binding.connEtPort.setText(SetViewModel.getPort())
        binding.connEtBike1.setText(SetViewModel.getBike1())
        binding.connEtBike2.setText(SetViewModel.getBike2())
        binding.connEtBattery.setText(SetViewModel.getBattery())
    }
    //
    override fun onResume() {
        super.onResume()
        binding.connBtnCard.setOnClickListener {
            //单车1
            val Bike1 =binding.connEtBike1.text.toString().trim()
            val Bike2 =binding.connEtBike2.text.toString().trim()
            val Battery =binding.connEtBattery.text.toString().trim()
            Log.d("bike1","bike1:(set)"+Bike1)
            SetViewModel.setBike1(Bike1)
            SetViewModel.setBike2(Bike2)
            SetViewModel.setBattery(Battery)
            //存入数据库的方法
            val values_bike1 = ContentValues().apply {
                //组装数据
                put("bike1", SetViewModel.getBike1())
                Log.d("存入bike1","已存入"+SetViewModel.getBike1())
            }
            DBViewModel.db?.insert("Bike1",null,values_bike1)
            ToastUtils.showToast(context,"卡号存入成功")
        }
    }

    /**
     * 校验服务器IP
     **/
    private fun checkIP(ip:String):Boolean {
        var isIP:Boolean = false

        if (!TextUtils.isEmpty(ip)) {
            //IP地址验证规则
            val regex:String = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"

            val pattern: Pattern = Pattern.compile(regex)
            val matcher: Matcher = pattern.matcher(ip)
            val rs:Boolean = matcher.matches()// 字符串是否与正则表达式相匹配
            isIP = rs
        }
        return isIP
    }

}