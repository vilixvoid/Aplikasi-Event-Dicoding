package com.dicoding.aplikasidicodingevent.data.local.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.aplikasidicodingevent.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event ORDER BY publishedAt DESC")
    fun getEvent(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE favorited = 1")
    fun getFavoritedEvent(): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvent(events: List<EventEntity>)

    @Update
    fun updateEvent(event: EventEntity)

    @Query("DELETE FROM event WHERE favorited = 0")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM event WHERE title = :title AND favorited = 1)")
    fun isEventFavorite(title: String): Boolean
}
