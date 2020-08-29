package com.networksample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.networksample.repository.StatusRepository


class MainViewModelFactory constructor(private val repository: StatusRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {

            when {
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository)

                else ->
                    throw IllegalArgumentException(
                        "Unknown ViewModel class: ${modelClass.name}"
                    )
            }

        } as T
    }
}