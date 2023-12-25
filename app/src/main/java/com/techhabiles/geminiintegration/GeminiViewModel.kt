package com.techhabiles.geminiintegration

import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 *  ViewModel for Gemini Integration, Viewmodel hold generative AI model  to interact with Gemini
 *  @author TechHabiles
 */

class GeminiViewModel(
    private val generativeModel: GenerativeModel
) : BaseViewModel() {
    private val _speak: MutableStateFlow<Boolean> =
        MutableStateFlow(false)

    val speak: StateFlow<Boolean> = _speak

    private val _response: MutableStateFlow<String> =
        MutableStateFlow("")

    val response: StateFlow<String> =
        _response.asStateFlow()

    private val _prompt: MutableStateFlow<String> =
        MutableStateFlow("")

    val prompt: StateFlow<String> =
        _prompt.asStateFlow()


    private  var chat: Chat? = null

    // Start chat session with Gemini
    fun initChat(){
        chat = Chat(generativeModel)
    }

    /**
     * Send chat message to Gemini
     */
    fun sendMessage(message: String){
        setResponse("${_response.value} Me: $message \n")
        setLoading(true)
        viewModelScope.launch {
            try{
                val resp =  chat?.sendMessage(message)
                resp?.text?.let{
                    setResponse("${_response.value} Gemini: $it \n")
                    setLoading(false)
                }
            }catch(_: Exception){
                setLoading(false)
            }

        }
    }

    /**
     * update Prompt text
     */
    fun setPrompt(prompt: String){
        _prompt.value = prompt
    }

    /**
     * Describe image to Gemini,
     * @param content, Holds image to describe
     */
    fun describeImage(content :Content){
        setLoading(true)
        clearResponse()
        viewModelScope.launch {
            try{
                val resp = generativeModel.generateContent(content)
                resp.text?.let{
                    setResponse(it)
                    setLoading(false)
                }
            }catch (ex: Exception){
                setLoading(false)
                setResponse(ex.localizedMessage ?: "")
            }

        }

    }

    /**
     *  Sets response received from Gemini to user interface
     */
    private fun setResponse(message: String){
        _response.value = message
    }

    fun speakText(){
        _speak.value = !(_speak.value)!!
    }

    /**
     *  Clear response received from Gemini to user interface
     */
    fun clearResponse(){
       setResponse("")
        _speak.value = false
    }

    /**
     *  Answer user inputs  using Gemini LLM model, this is text based only
     *  @param inputText, User input text
     */
    fun answerUserInputs(inputText: String) {
        setLoading(true)
        clearResponse()

        viewModelScope.launch {
            try {

                val resp = generativeModel.generateContent(inputText)
                resp.text?.let{
                    setResponse(it)
                }
                setLoading(false)
            } catch (e: Exception) {
                setLoading(false)
                setResponse(e.localizedMessage ?: "")
            }
        }
    }

    /**
     * Clear chat session or anything else that needs to be cleared
     */
    override fun onCleared() {
        super.onCleared()
        chat = null

    }
}