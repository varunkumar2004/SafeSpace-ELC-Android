package com.varunkumar.safespace.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.varunkumar.safespace.shared.SensorDataResponse
import com.varunkumar.safespace.shared.SharedViewModelData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val sharedViewModelData: SharedViewModelData
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    private val liveRecommendations = sharedViewModelData.liveRecommendations
    private val liveStressLevel = sharedViewModelData.liveStressLevel

    val state =
        combine(_state, liveRecommendations, liveStressLevel) { state, recommendations, stress ->
            _state.update {
                it.copy(
                    recommendedVideos = recommendations?.results,
                    stressLevel = stress?.stressLevel
                )
            }
            Log.d("home state change", _state.value.toString())
            state
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeState())

    init {
        getSenseValues()
    }

    private fun getSenseValues() {
        viewModelScope.launch {
            database.getReference("sensorData").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.value?.let { value ->
                            val gsonResponse =
                                Gson().fromJson(value.toString(), SensorDataResponse::class.java)
                            _state.update { it.copy(values = it.values + gsonResponse) }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.e("snapshot error", p0.message)
                    }
                }
            )
        }
    }
}