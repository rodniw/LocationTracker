package dev.example.ru.locationtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_ID = 0

@Entity(tableName = "distances")
data class LocationModel (val distance: String) {
    @PrimaryKey(autoGenerate = false)
    var id : Int = CURRENT_ID
}