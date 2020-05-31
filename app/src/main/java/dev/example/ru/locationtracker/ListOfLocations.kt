package dev.example.ru.locationtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_of_locations.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ListOfLocations : AppCompatActivity(), CoroutineScope {

    private var listOfLocationsAdapter = ListOfLocationsAdapter()

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_locations)
    }

    override fun onResume() {
        super.onResume()

        list_of_locations_recycler_view.layoutManager = LinearLayoutManager(this)
        list_of_locations_recycler_view.adapter = listOfLocationsAdapter
        listOfLocationsAdapter.locations = arrayListOf()

        launch {
            val result =  getLocations()
            onResult(result) // onResult is called on the main thread
        }
    }

    private fun onResult(locations: LiveData<List<LocationModel>>) {
        locations.observe(this, Observer {
            listOfLocationsAdapter.locations = it
            listOfLocationsAdapter.notifyItemInserted(listOfLocationsAdapter.locations.size)
        })
    }

    private suspend fun getLocations(): LiveData<List<LocationModel>> {
        return withContext(Dispatchers.IO) {
            return@withContext AppDatabase.getInstance(applicationContext).locationDao().getLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
