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

    companion object {
        private const val PUBLIC_API = "public api"
        private const val PRIVATE_API = "private api"
        private const val EMPTY = "empty"
    }

    private val _showToast = MutableLiveData<String>()
    val showToast: LiveData<String> = _showToast
    private var currentUrl: String = ""

    init {
        statusRepository.repositoryCallback =
            object : StatusRepository.RepositoryCallback<StatusData> {
                override fun onSuccess(data: StatusData) {
                    _showToast.value = "${getAPIName()} success : ${data.status} - ${data.message}"
                }

                override fun onFailed(exception: Throwable) {
                    _showToast.value = "${getAPIName()} failed"
                }

            }
    }

    fun requestData(currentUrl: String) {
        this.currentUrl = currentUrl
        statusRepository.getData(currentUrl)
    }

    private fun getAPIName(): String {
        return when (currentUrl) {
            PUBLIC_API_URL -> {
                PUBLIC_API
            }
            PRIVATE_API_URL -> {
                PRIVATE_API
            }
            else -> EMPTY
        }
    }
}