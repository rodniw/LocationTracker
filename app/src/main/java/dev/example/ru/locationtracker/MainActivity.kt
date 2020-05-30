package dev.example.ru.locationtracker

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val MY_PERMISSION_ACCESS_COARSE_LOCATION = 1
private const val MY_PERMISSION_ACCESS_FINE_LOCATION = 2

class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var getChangesListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        location_history_btn.setOnClickListener {
            startActivity(Intent(this, ListOfLocations::class.java))
        }

        location_start_tracking_btn.setOnClickListener{
            when ((it as Button).text) {
                getString(R.string.start_text) -> {
                    if (hasLocationPermission("$ACCESS_COARSE_LOCATION, $ACCESS_FINE_LOCATION")) {
                        bindLocationManager()
                    }
                    else requestLocationPermission(ACCESS_COARSE_LOCATION)
                }
                getString(R.string.stop_text) -> {
                    it.text = getString(R.string.start_text)
                    locationManager.removeUpdates(getChangesListener)
                    location_writing_text.visibility = View.GONE
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSION_ACCESS_COARSE_LOCATION) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (hasLocationPermission(ACCESS_FINE_LOCATION)) {
                    bindLocationManager()
                } else requestLocationPermission(ACCESS_FINE_LOCATION)
            else Toast.makeText(this, getString(R.string.set_location_manually_text), Toast.LENGTH_LONG).show()
        } else if(requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION)
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) bindLocationManager()
            else Toast.makeText(this, getString(R.string.set_location_manually_text), Toast.LENGTH_LONG).show()
    }

    @SuppressLint("MissingPermission")
    private fun bindLocationManager() {
        location_start_tracking_btn.text = getString(R.string.stop_text)
        location_writing_text.visibility = View.VISIBLE

        getChangesListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                Toast.makeText(applicationContext, location?.latitude.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("Not yet implemented")
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("Not yet implemented")
            }

        }
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 0F, getChangesListener)
    }

    private fun hasLocationPermission(permission: String): Boolean {
        return (ContextCompat.checkSelfPermission(this, permission/*ACCESS_COARSE_LOCATION*/) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission(permission: String) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            MY_PERMISSION_ACCESS_COARSE_LOCATION
        )
    }
}
