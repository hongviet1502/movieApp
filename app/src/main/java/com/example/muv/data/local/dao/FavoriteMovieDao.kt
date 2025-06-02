package com.example.muv.data.local.dao

import androidx.room.*
import com.example.muv.data.local.entity.FavoriteMovieEntity

@Dao
interface FavoriteMovieDao {

    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    suspend fun getAllFavorites(): List<FavoriteMovieEntity>

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId")
    suspend fun getFavoriteById(movieId: Int): FavoriteMovieEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    suspend fun isFavorite(movieId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(movie: FavoriteMovieEntity)

    @Delete
    suspend fun deleteFavorite(movie: FavoriteMovieEntity)

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavoriteById(movieId: Int)

    @Query("DELETE FROM favorite_movies")
    suspend fun deleteAllFavorites()

    @Query("SELECT COUNT(*) FROM favorite_movies")
    suspend fun getFavoritesCount(): Int
}