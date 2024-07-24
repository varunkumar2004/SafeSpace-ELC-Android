package com.varunkumar.safespace.shared

import com.google.gson.annotations.SerializedName

data class NlpResponse(
    @SerializedName("Results") val results: List<String>
)