package com.dicoding.aplikasidicodingevent.data

sealed class ResultEvent<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultEvent<T>()
    data class Error(val error: String) : ResultEvent<Nothing>()
    object Loading : ResultEvent<Nothing>()
}