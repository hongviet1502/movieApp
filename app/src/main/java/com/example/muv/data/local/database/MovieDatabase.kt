package com.example.muv.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.muv.data.local.dao.MovieDao
import com.example.muv.data.local.entity.FavoriteMovie

@Database(
    entities = [FavoriteMovie::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        const val DATABASE_NAME = "movie_database"
    }
}