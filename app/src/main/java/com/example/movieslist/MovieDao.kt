package com.example.movieslist2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getAll(): List<Movie>

    @Insert
    fun insert(movie: Movie)

    @Delete
    fun delete(movie: Movie)

    @Update
    fun update(movie: Movie)
}