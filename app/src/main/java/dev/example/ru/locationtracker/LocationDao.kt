package dev.example.ru.locationtracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun upsert(location: LocationModel)

    @Query("SELECT * FROM distances")
    fun getLocation(): LiveData<List<LocationModel>>

    @Query("DELETE FROM distances")
    fun cleanTable()
}