package com.timeskip.ezlink

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val sharedInfoMutableLiveData: MutableLiveData<ShareInfoModel?> = MutableLiveData(null)
    val sharedInfoLiveData: LiveData<ShareInfoModel?> = sharedInfoMutableLiveData

    fun setSharedUrl(shareInfoModel: ShareInfoModel?) {
        sharedInfoMutableLiveData.value = shareInfoModel
    }

    fun reset() {
        sharedInfoMutableLiveData.value = null
    }
}