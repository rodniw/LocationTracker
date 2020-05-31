package dev.example.ru.locationtracker

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt


private const val MY_PERMISSION_ACCESS_COARSE_LOCATION = 1
private const val MY_PERMISSION_ACCESS_FINE_LOCATION = 2

class MainActivity : AppCompatActivity(), CoroutineScope {

    private var summ = 0

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var locationManager: LocationManager
    private lateinit var getChangesListener: LocationListener

    private var prevLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        location_history_btn.setOnClickListener {
            startActivity(Intent(this, ListOfLocations::class.java))
        }

        location_history_clean_btn.setOnClickListener {
            launch {
                cleanDB()
            }
            summ = 0
            location_summary.text = "Пройденное расстояние: 0"
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

    private suspend fun cleanDB() {
        return withContext(Dispatchers.IO) {
            return@withContext AppDatabase.getInstance(applicationContext).locationDao().cleanTable()
        }
    }

    private suspend fun insertDBTask(context: Context, distance: Int) {
        return withContext(Dispatchers.IO) {
            return@withContext AppDatabase.getInstance(context).locationDao().upsert(
                LocationModel(distance.toString())
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun bindLocationManager() {
        location_start_tracking_btn.text = getString(R.string.stop_text)
        location_writing_text.visibility = View.VISIBLE

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.powerRequirement = Criteria.POWER_HIGH
        criteria.isAltitudeRequired = false
        criteria.isSpeedRequired = false
        criteria.isCostAllowed = true
        criteria.isBearingRequired = false
        criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH

        getChangesListener = object : LocationListener {
            @SuppressLint("SetTextI18n")
            override fun onLocationChanged(location: Location?) {
                prevLocation?.let { prevLocation ->
                    location?.let { currentLocation ->
                        val distance = currentLocation.distanceTo(prevLocation).roundToInt()
                        launch {
                            insertDBTask(applicationContext, distance)
                        }
                        Toast.makeText(applicationContext, "$distance метра(-ов)", Toast.LENGTH_LONG).show()
                        summ += distance
                        location_summary.text = "Пройденное расстояние: $summ метра(-ов)"
                    } ?: run {  }
                } ?: run { /*Toast.makeText(applicationContext, "start point is: ${prevLocation.latitude}", Toast.LENGTH_LONG).show()*/ }
                prevLocation = location
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String?) {}
            override fun onProviderDisabled(provider: String?) {}

        }
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        prevLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        //long minTime, float minDistance, @NonNull Criteria criteria, @NonNull LocationListener listener, @Nullable Looper looper
        locationManager.requestLocationUpdates(10000L, 0F, criteria, getChangesListener, null)
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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
