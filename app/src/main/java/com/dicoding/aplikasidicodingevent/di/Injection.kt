package com.dicoding.aplikasidicodingevent.di

import android.content.Context
import com.dicoding.aplikasidicodingevent.data.local.Room.EventDatabase
import com.dicoding.aplikasidicodingevent.data.remote.retrofit.ApiConfig
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import com.dicoding.aplikasidicodingevent.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.create()
        val database = EventDatabase.getDatabase(context)
        val dao = database.eventDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, dao, appExecutors)
    }
}