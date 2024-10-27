package com.dicoding.aplikasidicodingevent.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.aplikasidicodingevent.data.ResultEvent
import com.dicoding.aplikasidicodingevent.data.local.Room.EventDao
import com.dicoding.aplikasidicodingevent.data.local.entity.EventEntity
import com.dicoding.aplikasidicodingevent.data.remote.response.EventResponse
import com.dicoding.aplikasidicodingevent.data.remote.retrofit.ApiService
import com.dicoding.aplikasidicodingevent.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<ResultEvent<List<EventEntity>>>()

    fun getHeadlineEvent(): LiveData<ResultEvent<List<EventEntity>>> {
        result.value = ResultEvent.Loading
        val client = apiService.getEvents(active = 1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val listEvents = response.body()?.listEvents
                    val eventList = ArrayList<EventEntity>()
                    appExecutors.diskIO.execute {
                        listEvents?.forEach { eventItem ->
                            val isFavorited = eventDao.isEventFavorite(eventItem.name ?: "")
                            val event = EventEntity(
                                title = eventItem.name ?: "",
                                publishedAt = eventItem.beginTime ?: "",
                                urlToImage = eventItem.imageLogo ?: "",
                                url = eventItem.link ?: "",
                                isFavorited
                            )
                            eventList.add(event)
                        }
                        eventDao.deleteAll()
                        eventDao.insertEvent(eventList)
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                result.value = ResultEvent.Error(t.message.toString())
            }
        })

        val localData = eventDao.getEvent()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = ResultEvent.Success(newData)
        }
        return result
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, appExecutors)
            }.also { instance = it }
    }
}


