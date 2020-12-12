package com.kk.travelbookkotlin

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class CustomAdapter(val placeList: ArrayList<Place>,val context: Activity) :
    ArrayAdapter<Place>(context, R.layout.custom_list_row, placeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater=context.layoutInflater
        val customView=layoutInflater.inflate(R.layout.custom_list_row,null,true)


        return super.getView(position, convertView, parent)
    }
}