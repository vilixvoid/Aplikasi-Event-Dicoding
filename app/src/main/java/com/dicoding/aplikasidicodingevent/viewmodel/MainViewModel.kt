package com.dicoding.aplikasidicodingevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.aplikasidicodingevent.data.response.EventResponse
import com.dicoding.aplikasidicodingevent.data.response.ListEventsItem
import com.dicoding.aplikasidicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainViewModel : ViewModel() {

    private val _activeEvents = MutableLiveData<List<ListEventsItem>>()
    val activeEvents: LiveData<List<ListEventsItem>> = _activeEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchEvents(1)
        fetchEvents(0)
    }

    fun fetchEvents(active: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.create().getEvents(active)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    when (active) {
                        1 -> _activeEvents.value = response.body()?.listEvents ?: emptyList()
                        0 -> _finishedEvents.value = response.body()?.listEvents ?: emptyList()
                    }
                } else {
                    _errorMessage.value = "Terjadi kesalahan: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                handleError(client, t)
            }
        })
    }



    fun searchEvents(query: String) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.create().searchEvents(active = -1, query = query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal mencari event"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                handleError(client, t)
            }
        })
    }

    private fun handleError(call: Call<EventResponse>, t: Throwable) {
        try {
            val response = call.execute()
            val message = when {
                !response.isSuccessful && response.code() == 404 -> "Tidak ditemukan data"
                !response.isSuccessful && response.code() == 500 -> "Server dalam bermasalah"
                t is UnknownHostException -> "Maaf, Internet lambat atau terputus"
                t is SocketTimeoutException -> "Koneksi Internet Anda sangat lambat"
                else -> "Telah terjadi kesalahan: ${t.localizedMessage}"
            }
            _errorMessage.value = message
        } catch (e: Exception) {
            _errorMessage.value = "Telah terjadi kesalahan: ${e.localizedMessage}"
            Log.e("MainViewModel", "handleError: ${e.message}")
        }
        Log.e("MainViewModel", "onFailure: ${t.message}")
    }
}