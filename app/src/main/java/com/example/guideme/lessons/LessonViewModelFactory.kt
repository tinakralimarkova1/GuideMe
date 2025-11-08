package com.example.guideme.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LessonViewModelFactory(
    private val repo: LessonsRepository,
    private val userEmail: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LessonViewModel(repo, userEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

