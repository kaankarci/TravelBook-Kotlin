package com.kk.travelbookkotlin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    val placesArray = ArrayList<Place>()
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.add_location_item){
            val intent=Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.add_location,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val database=openOrCreateDatabase("Places",Context.MODE_PRIVATE,null)
            var cursor = database.rawQuery("SELECT * FROM places",null)
            val adressIndex=cursor.getColumnIndex("adress")
            val latitudeIndex=cursor.getColumnIndex("latitude")
            val longitudeIndex=cursor.getColumnIndex("longitude")
            while (cursor.moveToNext()){
                val adressFromDatabase = cursor.getString(adressIndex)
                val latitudeFromDatabase=cursor.getDouble(latitudeIndex)
                val longitudeFromDatabase=cursor.getDouble(longitudeIndex)
                val myPlace=Place(adressFromDatabase,latitudeFromDatabase,longitudeFromDatabase)
                println(myPlace.adress)
                placesArray.add(myPlace)
            }

cursor.close()
        }catch (e:Exception){e.printStackTrace()}

    }
}