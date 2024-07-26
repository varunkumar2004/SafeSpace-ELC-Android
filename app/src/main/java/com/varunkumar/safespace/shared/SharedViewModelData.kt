package com.varunkumar.safespace.shared

import kotlinx.coroutines.flow.MutableStateFlow

data class SharedViewModelData(
    val liveStressLevel: MutableStateFlow<StressLevelResponse?> = MutableStateFlow(null),
    val liveCameraResult: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val liveRecommendations: MutableStateFlow<NlpResponse?> = MutableStateFlow(null)
)
