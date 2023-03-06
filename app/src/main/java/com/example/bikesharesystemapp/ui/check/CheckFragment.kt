package com.example.bikesharesystemapp.ui.check

//import com.example.farmingsystemapp.MainViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bikesharesystemapp.MainViewModel
import com.example.bikesharesystemapp.databinding.CheckFragmentBinding
import com.example.bikesharesystemapp.popupWindow.LineChart
import com.example.bikesharesystemapp.util.DataHandle
import com.example.bikesharesystemapp.utils.DHModel
import com.example.bikesharesystemapp.utils.DataSendLight

//import com.example.farmingsystemapp.popupWindow.LineChart
//import com.example.farmingsystemapp.utils.DataHandle

class CheckFragment : Fragment(){

//    启用ViewBinding功能之后，则必然会生成一个与其对应的CheckFragmentBinding类
    private var _binding:CheckFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var mainActivity : com.example.bikesharesystemapp.MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CheckFragmentBinding.inflate(inflater,container,false)

        mainActivity = activity as com.example.bikesharesystemapp.MainActivity

        initView()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun initView() {
        //设置保存的值,从共享数据中拿,不然切换页面的话，原来获取到的数据又会消失
        binding.connTvGps.text="${CheckViewModel.getGps()} "
        binding.connTvMotorSpeed.text="${CheckViewModel.getMotorspeed()} "
        binding.connTvIllu.text="${CheckViewModel.getIllu()} "
        binding.connTvBikeid.text="${CheckViewModel.getBikeid()} "
        binding.connTvBatteryid.text="${CheckViewModel.getBatteryid()} "
        binding.connTvV.text="${CheckViewModel.getV()} "
        binding.connTvI.text="${CheckViewModel.getI()} "
        binding.connTvT.text="${CheckViewModel.getT()} "
        Log.d("checkBikemount","checkBikemount:"+DHModel.getBikeMount())
        binding.connTvBikemount.text= "${DHModel.getBikeMount()}"

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val dataHandle = DataHandle(mainActivity, requireView())

        //发送数据帧类
        val dataSend = DataSendLight(mainActivity)

        //当服务器传来数据帧，receDate值发生改变调用
        MainViewModel.receDate.observe(viewLifecycleOwner, Observer {

//            if(it != MainViewModel.oldReceDate){
                //弊端：当连续接收同样的数据时，无变化，但是每次的数据都需要处理，导致不能处理无变化的数据
                dataHandle.handle(it)
                MainViewModel.oldReceDate = it

//            }

        } )

        fun bikemount(recvDate:String){
            try {
                val typebike=recvDate.substring(15,17) //"CA"或者“1B”
                when(typebike){
                    "CA"->{

                    }
                }
            }catch ( e:Exception) {
                e.printStackTrace()
            }
        }
        binding.t.setOnClickListener {
            val lineChat=LineChart(mainActivity)
            lineChat.setYHight(12,1)
            lineChat.setYName("温差：℃")
            lineChat.showChart()
            CheckViewModel.isTLineChartOpen =true
        }
        binding.v.setOnClickListener {
            val lineChart=LineChart(mainActivity)
            lineChart.setYHight(6,1)
            lineChart.setYName("电压：v")
            lineChart.showChart()
            CheckViewModel.isVLineChartOpen =true
        }
        binding.ie.setOnClickListener {
            val lineChart = LineChart(mainActivity)
            lineChart.setYHight(2000,300)
            lineChart.setYName("光照: lux")
            lineChart.showChart()
            CheckViewModel.isIlluLineChartOpen = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //保证binding有效生命周期是在onCreateView()函数和onDestroyView()函数之间
        _binding = null
    }


}