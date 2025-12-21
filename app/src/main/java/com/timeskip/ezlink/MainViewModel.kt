package com.timeskip.ezlink

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val sharedUrlMutableLiveData: MutableLiveData<String?> = MutableLiveData(null)
    val sharedUrlLiveData: LiveData<String?> = sharedUrlMutableLiveData

    private val sharedTagNameMutableLiveData: MutableLiveData<String?> = MutableLiveData(null)
    val sharedTagNameLiveData: LiveData<String?> = sharedTagNameMutableLiveData

    fun setSharedUrl(url: String?) {
        sharedUrlMutableLiveData.value = url
    }

    fun setSharedTagName(tagName: String?) {
        sharedTagNameMutableLiveData.value = tagName
    }

    fun reset() {
        sharedUrlMutableLiveData.value = null
        sharedTagNameMutableLiveData.value = null
    }
}