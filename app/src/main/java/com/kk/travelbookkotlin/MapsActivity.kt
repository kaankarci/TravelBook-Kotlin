package com.kk.travelbookkotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    var firstTime = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener ( myListener )



        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (location != null) {
                    val sharedPreferences = this@MapsActivity.getSharedPreferences(
                        "com.kk.travelbookkotlin",
                        Context.MODE_PRIVATE
                    )

                    if (firstTime == 1) {
                        mMap.clear()
                        val newUserLocation = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(newUserLocation).title("I'm Here"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newUserLocation, 15f))
                        firstTime = 0
                    }
                }
            }

        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2,
                2f,
                locationListener
            )

            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                val lastLocationLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatLng, 15f))
                println("geçerli konuma gidildi")
            }

        }
    }

    val myListener = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng?) {


            val geoCoder = Geocoder(this@MapsActivity, Locale.getDefault())
            var adress = ""
            if (p0 != null) {
                val adressList = geoCoder.getFromLocation(p0.latitude, p0.longitude, 1)
                if (adressList != null && adressList.size > 0) {
                    if (adressList[0].thoroughfare != null) {adress += adressList[0].thoroughfare+","
                        if (adressList[0].subThoroughfare != null) {
                            adress += adressList[0].subThoroughfare
                        }
                    }
                } else {
                    adress = "New Place"
                }
mMap.clear()
                mMap.addMarker(MarkerOptions().position(p0).title(adress))
                val newPlace=Place(adress,p0.latitude,p0.longitude)
                val dialog = AlertDialog.Builder(this@MapsActivity)
                dialog.setCancelable(false)
                dialog.setTitle("Are You Sure?")
                dialog.setMessage(newPlace.adress)
                dialog.setPositiveButton("Yes") { dialog, which ->
                    //database ye eklemek
                   try {


                    val database=openOrCreateDatabase("Places",Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS places(adress VARCHAR, latitude DOUBLE,longitude DOUBLE)")
                    val toCompile="INSERT INTO places(adress, latitude, longitude) Values(?,?,?)"
                    val sqLiteStatement= database.compileStatement(toCompile)
                    sqLiteStatement.bindString(1,newPlace.adress)
                    sqLiteStatement.bindDouble(2,newPlace.latitude!!)
                    sqLiteStatement.bindDouble(3,newPlace.longitude!!)
                    sqLiteStatement.execute()
                    Toast.makeText(this@MapsActivity,"New Place Created",Toast.LENGTH_LONG).show()
                   }catch (e:Exception){e.printStackTrace()}
                }
                    .setNegativeButton("No") { dialog, which ->
                        Toast.makeText(
                            this@MapsActivity,
                            "Canceled!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                dialog.show()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2,
                        2f,
                        locationListener
                    )

                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}