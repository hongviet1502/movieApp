package com.example.muv.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.muv.data.local.dao.FavoriteMovieDao
import com.example.muv.data.local.entity.FavoriteMovieEntity

@Database(
    entities = [FavoriteMovieEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun favoriteMovieDao(): FavoriteMovieDao

    companion object {
        const val DATABASE_NAME = "movie_database"
    }
}