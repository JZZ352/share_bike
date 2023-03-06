package com.example.bikesharesystemapp.utils

import androidx.lifecycle.ViewModel

object DHModel : ViewModel() {
    private var bikemount ="0"//单车的在线个数
    //单车在线个数
    fun getBikeMount():String{
        return this.bikemount
    }
    fun setBikeMount(bikemount:String){
        this.bikemount=bikemount
    }
}