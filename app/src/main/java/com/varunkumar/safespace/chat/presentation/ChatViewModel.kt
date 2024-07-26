package com.varunkumar.safespace.chat.presentation

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.varunkumar.safespace.chat.data.ChatMessage
import com.varunkumar.safespace.chat.domain.NlpModelApi
import com.varunkumar.safespace.shared.NlpResponse
import com.varunkumar.safespace.shared.SharedViewModelData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val model: GenerativeModel,
    private val nlpModelApi: NlpModelApi,
    private val sharedViewModelData: SharedViewModelData
) : ViewModel() {
    private var textToSpeech: TextToSpeech? = null

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val chat = model.startChat(
        listOf(
            content("user") {
                text(
                    "strictly talk about stress detection and management not about any other topic; " +
                            "also keep the answer short"
                )
            }
        )
    )


    init {
        textToSpeech = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
            } else {
                Log.e("TTS", "TTS initialization failed")
            }
        }
    }

    private fun speakOutText(message: ChatMessage) {
        _state.update { it.copy(speakText = message) }
        textToSpeech?.speak(message.data, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun predictNlp() {
        val inputList = _state.value.messages.filter { !it.isBot }.map { it.data }

        nlpModelApi.analyzeText(inputList).enqueue(
            object : Callback<NlpResponse?> {
                override fun onResponse(
                    p0: Call<NlpResponse?>,
                    response: Response<NlpResponse?>
                ) {
                    viewModelScope.launch {
                        response.body()?.let { nlpResponse ->
                            sharedViewModelData.liveRecommendations.emit(nlpResponse)
                        }
                    }
                }

                override fun onFailure(p0: Call<NlpResponse?>, p1: Throwable) {
                    Log.e(
                        "nlp response",
                        p1.localizedMessage ?: "Some error occurred while fetching nlp response"
                    )
                }
            }
        )
    }

    fun onMessageChange(newMessage: String) {
        _state.update { it.copy(message = newMessage) }
    }

    fun sendPrompt() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    uiState = UiState.Loading,
                    messages = it.messages + ChatMessage(data = _state.value.message, isBot = false)
                )
            }

            try {
                chat.sendMessage(_state.value.message).text?.let { outputContent ->
                    val newMessage = ChatMessage(
                        data = outputContent.trimIndent(),
                        isBot = true
                    )

                    _state.update {
                        it.copy(
                            message = "",
                            messages = it.messages + newMessage,
                            uiState = UiState.Success(outputContent, newMessage.timestamp)
                        )
                    }

                    speakOutText(newMessage)
                    predictNlp()
                }
            } catch (e: Exception) {
                _state.update { it.copy(uiState = UiState.Error(e.message)) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        textToSpeech?.apply {
            stop()
            shutdown()
        }
    }
}

