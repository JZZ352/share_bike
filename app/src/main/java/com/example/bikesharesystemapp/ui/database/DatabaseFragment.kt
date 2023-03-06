package com.example.bikesharesystemapp.ui.database

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bikesharesystemapp.R
import com.example.bikesharesystemapp.databinding.DatabaseFragmentBinding
import com.example.bikesharesystemapp.entity.SensorAdapter
import com.example.bikesharesystemapp.entity.SensorData
import com.example.connectservera.utils.ToastUtils
import java.text.SimpleDateFormat
import java.util.*

class DatabaseFragment : Fragment() {
    //与页面切换相关
    private var _binding: DatabaseFragmentBinding? = null

    private val binding get() = _binding!!

    //选择日期Dialog
    private var datePickerDialog: DatePickerDialog? = null

    private val calendar: Calendar  = Calendar.getInstance()

    private lateinit var sensorAdapter: SensorAdapter
//
    override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View {
        //resource ：布局的资源id
        //root ：填充的根视图
        //attachToRoot ：是否将载入的视图绑定到根视图中
        _binding = DatabaseFragmentBinding.inflate(inflater, container, false)

        //查询数据库的所有数据
        DBViewModel.cursor =
            DBViewModel.db?.query("Sensor",null,null,null,null,null,null)

        val layoutManager = LinearLayoutManager(context)

        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.stackFromEnd = true;//列表再底部开始展示，反转后由上面开始展示
        layoutManager.reverseLayout = true;//列表翻转

        binding.tableRecyclerView.layoutManager = layoutManager

        //初始化传感数据列表
        initSensor()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val sensorType = resources.getStringArray(R.array.sensorArray)
        //获取筛选的传感器类型
        binding.typeSelect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                Log.d("测试",sensorType[position])
                //如果不是选定传感器这个选项就将这个类型传递到数据库中保存
                if(sensorType[position] != "传感器")
                    DBViewModel.type = sensorType[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        //当点击就获取日期的弹窗界面
        binding.dateSelect.setOnClickListener {
            showDailog()
        }

        //开始筛选
        binding.selectBtn.setOnClickListener {
            val date = DBViewModel.time
            val type = DBViewModel.type

            Log.d("测试","date："+date)
            Log.d("测试","type："+type)

            queryFromDate(date, type)
        }

        //删除选定日期前的数据
        binding.deleteBtn.setOnClickListener {

           DBViewModel.db?.delete("Sensor",null, null)
            updateView()//更新列表

            ToastUtils.showToast(context,"数据删除成功！")
        }
    }

//  @SuppressLint("Range", "NotifyDataSetChanged")
    fun updateView(){
        //移除所有list数据
        DBViewModel.sensorList.clear()

        //重新查一遍数据
        DBViewModel.cursor =
            DBViewModel.db?.query("Sensor",null,null,null,null,null,null)

        DBViewModel.cursor?.let {
            Log.d("测试","Cursor:执行")
            if(it.moveToFirst()){
                do{
                    //遍历cursor对象，取出数据并添加至sensorList中
                    val cursorDate = it.getString(it.getColumnIndex("date"))
                    val cursorType = it.getString(it.getColumnIndex("type"))
                    val data = it.getString(it.getColumnIndex("data"))

                    DBViewModel.sensorList.add(SensorData(cursorDate,cursorType, data))

                }while(it.moveToNext())
            }

        }

        //让recyclerView使用自定义的适配器，生成相应列表
        sensorAdapter.setList(DBViewModel.sensorList)
        sensorAdapter.notifyDataSetChanged() // 通知数据变化
        binding.tableRecyclerView.adapter = sensorAdapter
    }
//    //按该日期和传感器类型开始筛选
    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun queryFromDate(date:String, type:String){
        //若日期在所选日期之后，加入list
        Log.d("测试","sensorList:"+DBViewModel.sensorList.toString())
        //移除所有list数据
        DBViewModel.sensorList.clear()
        var situation = 0
        situation = if(DBViewModel.time == "" && DBViewModel.type==""){
            1
        }else if (DBViewModel.time != "" && DBViewModel.type==""){
            2
        }else if (DBViewModel.time == "" && DBViewModel.type!= "" ){
            3
        }else {
            4
        }
        DBViewModel.cursor?.let {
            Log.d("测试","Cursor:执行")
            if(it.moveToFirst()){
                do{
                    //遍历cursor对象，取出数据并添加至sensorList中
                    val cursorDate = it.getString(it.getColumnIndex("date"))
                    val cursorType = it.getString(it.getColumnIndex("type"))
                    val data = it.getString(it.getColumnIndex("data"))
                    //查询的四种情况
                    when(situation){
                        //什么也没筛选
                        1->{
                            Log.d("测试","无筛选")
                        }
                        //时间为条件筛选
                        2->{
                            //筛选选定日期之后的数据
                            if(compareDate(cursorDate,date)){
                                DBViewModel.sensorList.add(SensorData(cursorDate,cursorType, data))
                            }
                        }
                        //传感器类型为条件筛选
                        3->{
                            if(cursorType == type){
                                DBViewModel.sensorList.add(SensorData(cursorDate,cursorType, data))
                            }
                        }
                        //两个条件都有筛选
                        4->{
                            if(compareDate(cursorDate,date) && cursorType == type){
                                DBViewModel.sensorList.add(SensorData(cursorDate,cursorType, data))
                            }
                        }
                    }

                }while(it.moveToNext())
            }
        }
        //让recyclerView使用自定义的适配器，生成相应列表
        sensorAdapter.setList(DBViewModel.sensorList)
        sensorAdapter.notifyDataSetChanged() // 通知数据变化
        binding.tableRecyclerView.adapter = sensorAdapter
        ToastUtils.showToast(context,"查找数据成功！")

    }
    //比较两个日期，通过时间戳
    @SuppressLint("SimpleDateFormat")
    fun compareDate(date1:String,date2:String): Boolean {
        val pattern = "yyyy.MM.dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        //将日期字符串转换为日期格式
        val d1 = simpleDateFormat.parse(date1)
        val d2 = simpleDateFormat.parse(date2)

        //获取两个日期格式的时间戳
        val timeStamp1 = d1.time
        val timeStamp2 = d2.time

        //若日期1在日期2之后，返回true
        return timeStamp1>=timeStamp2
    }
    //显示数据库的表格
    @SuppressLint("Range")
    private fun initSensor(){
        //先清空，在添加
        DBViewModel.sensorList.clear()

        DBViewModel.cursor?.let {
            if(it.moveToFirst()){
                do{
                    //遍历cursor对象，取出数据并添加至sensorList中
                    val date = it.getString(it.getColumnIndex("date"))
                    val type = it.getString(it.getColumnIndex("type"))
                    val data = it.getString(it.getColumnIndex("data"))

                    DBViewModel.sensorList.add(SensorData(date,type, data))
                }while(it.moveToNext())
            }
        }

        //让recyclerView使用自定义的适配器，生成相应列表
        sensorAdapter = SensorAdapter(DBViewModel.sensorList)
        binding.tableRecyclerView.adapter = sensorAdapter

    }
    //显示日历控件
    private fun showDailog() {
        datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth -> //monthOfYear 得到的月份会减1所以我们要加1
                val time =
                    year.toString() + "." + (monthOfYear + 1).toString() + "." + dayOfMonth.toString()
                Log.d("测试", time)

                //将当前选择的日期显示
                binding.stDateShow.text = time

                //将当前选择的日期存储
                DBViewModel.time = time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        )
        datePickerDialog!!.show()
        //自动弹出键盘问题解决
        datePickerDialog!!.window!!
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
//
    }

}