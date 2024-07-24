package com.varunkumar.safespace.shared

import kotlinx.coroutines.flow.MutableStateFlow

data class SharedViewModelData(
    val liveStressLevel: MutableStateFlow<StressLevelResponse?> = MutableStateFlow(null),
    val liveRecommendations: MutableStateFlow<NlpResponse?> = MutableStateFlow(null)
)
