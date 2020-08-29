package com.networksample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.networksample.APIUtils.Companion.PRIVATE_API_URL
import com.networksample.APIUtils.Companion.PUBLIC_API_URL
import com.networksample.repository.StatusRepository
import com.networksample.repository.remote.data.StatusData

class MainViewModel(private val statusRepository: StatusRepository) :
    ViewModel() {

    private val _showToast = MutableLiveData<String>()
    val showToast: LiveData<String> = _showToast
    private var currentUrl: String = ""

    init {
        statusRepository.repositoryCallback = object : StatusRepository.RepositoryCallback<StatusData> {
            override fun onSuccess(data: StatusData) {
                val api = when (currentUrl) {
                    PUBLIC_API_URL -> {
                        "public api"
                    }
                    PRIVATE_API_URL -> {
                        "private api"
                    }
                    else -> "empty"
                }
                _showToast.value = "$api success : ${data.status} - ${data.message}"
            }

            override fun onFailed(exception: Throwable) {

            }

        }
    }

    fun requestData(currentUrl: String) {
        this.currentUrl = currentUrl
        statusRepository.getData(currentUrl)
    }
}