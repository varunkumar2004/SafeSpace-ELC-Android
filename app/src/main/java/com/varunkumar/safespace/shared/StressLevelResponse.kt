package com.varunkumar.safespace.shared

import com.google.gson.annotations.SerializedName

data class StressLevelResponse(
    @SerializedName("Result") var stressLevel: String
)
