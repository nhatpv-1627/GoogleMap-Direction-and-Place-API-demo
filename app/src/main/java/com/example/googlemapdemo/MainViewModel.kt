package com.example.googlemapdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlemapdemo.Utils.ProcessState
import com.example.googlemapdemo.model.DirectionResponse
import com.example.googlemapdemo.model.SearchResponse
import com.example.googlemapdemo.network.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel : ViewModel() {
    private val repository = Repository()
    val directionState = MutableLiveData<ProcessState<DirectionResponse>>()
    val searchPlaceState = MutableLiveData<ProcessState<SearchResponse>>()

    val textChangeSharedFlow = MutableSharedFlow<String>(replay = 0)

    init {
        viewModelScope.launch {
            textChangeSharedFlow.debounce(SEARCH_TIME_DELAY).collectLatest {
                searchPlace(it)
            }
        }
    }

    fun getDirectionsFromTo(from: LatLng, to: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            directionState.postValue(ProcessState.loading())
            try {
                val response = repository.getDirectionFromTo(BuildConfig.MAPS_API_KEY, from, to)
                withContext(Dispatchers.Main) {
                    directionState.postValue(ProcessState.success(response))
                }
            } catch (e: Exception) {
                directionState.postValue(ProcessState.error(e))
            }
        }
    }

    fun searchPlace(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    repository.searchPlace(BuildConfig.MAPS_API_KEY, query.replace(" ", "+"))
                withContext(Dispatchers.Main) {
                    searchPlaceState.value = ProcessState.success(response)
                }
            } catch (e: Exception) {
                searchPlaceState.postValue(ProcessState.error(e))
            }
        }
    }

    companion object {
        private const val SEARCH_TIME_DELAY = 300L
    }
}
