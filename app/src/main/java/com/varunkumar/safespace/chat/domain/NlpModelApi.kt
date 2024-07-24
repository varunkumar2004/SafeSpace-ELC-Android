package com.varunkumar.safespace.chat.domain

import com.varunkumar.safespace.shared.NlpResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NlpModelApi {
    @FormUrlEncoded
    @POST("/")
    fun analyzeText(
        @Field("text") text: List<String>
    ): Call<NlpResponse>
}
