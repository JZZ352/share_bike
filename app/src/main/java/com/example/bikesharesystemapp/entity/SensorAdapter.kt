package com.example.bikesharesystemapp.entity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bikesharesystemapp.R
//数据库的传感器部分

class SensorAdapter(var sensorList: List<SensorData>):
    RecyclerView.Adapter<SensorAdapter.ViewHolder>(){
    //获取数据库显示的三个控件，方便下面的直接调用（内部类：要在绑定控件时判断）
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val date = view.findViewById<TextView>(R.id.table_date)
        val type = view.findViewById<TextView>(R.id.table_type)
        val data = view.findViewById<TextView>(R.id.table_data)
    }
    //定义一个发送表的函数
    fun setList(list: List<SensorData>){
        this.sensorList = list
    }
    //数据库显示内容（将Holder与要显示的界面绑定起来）
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_item,parent,false)
        return ViewHolder(view)//这里的ViewHolder指代table_item了
    }
    //获取到的值存入
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //将Holder与控件进行绑定
        val sensorData = sensorList[position]
        holder.date.text = sensorData.date
        holder.type.text = sensorData.type
        holder.data.text = sensorData.data
    }
    //定义表中item的数量
    override fun getItemCount() = sensorList.size//这里将表中Item的数量设定未表的大小
}