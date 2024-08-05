package com.varunkumar.safespace.di

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.database.FirebaseDatabase
import com.varunkumar.safespace.R
import com.varunkumar.safespace.chat.domain.NlpModelApi
import com.varunkumar.safespace.sense.camera.domain.EmotionDetectionModelImpl
import com.varunkumar.safespace.sense.sensor.domain.StressModelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    @ViewModelScoped
    fun provideEmotionDetectionModel(@ApplicationContext context: Context): EmotionDetectionModelImpl {
        return EmotionDetectionModelImpl(context)
    }

    @Provides
    @ViewModelScoped
    fun provideStressModel(@ApplicationContext context: Context): StressModelApi {
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.sense_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StressModelApi::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideNlpModel(@ApplicationContext context: Context): NlpModelApi {
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.nlp_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NlpModelApi::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @ViewModelScoped
    fun provideGenerativeModel(@ApplicationContext context: Context): GenerativeModel {
        val safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.LOW_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.LOW_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.LOW_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.LOW_AND_ABOVE)
        )

        val model = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = context.getString(R.string.gen_model_api_key),
            generationConfig = generationConfig {
                temperature = 1f
                topK = 64
                topP = 0.95f
                maxOutputTokens = 8192
            },
            safetySettings = safetySettings
        )

        return model
    }
}