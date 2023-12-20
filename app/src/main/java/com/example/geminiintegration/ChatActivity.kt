package com.example.geminiintegration
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import java.util.Locale

/**
 *  ChatActivity to provide multi turn conversation with Gemini
 *  @author TechHabiles
 */
class ChatActivity : BaseActivity() {
    lateinit var viewModel: GeminiViewModel
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Gemini api model initializtion
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey
        )
        viewModel = GeminiViewModel(generativeModel)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {}

            override fun onResults(results: Bundle?) {
                val data: ArrayList<String>? = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                viewModel.setPrompt(data!![0])
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}

        })
        // Initializing the chat
        viewModel.initChat()
    }
    override fun getDataViewModel(): BaseViewModel {
        return viewModel
    }

    @Composable
    override fun ScreenContent(){
        ChatScreen(viewModel, speechRecognizer, onSummarizeClicked = { inputText ->
            viewModel.sendMessage(inputText)
        })
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    viewModel: GeminiViewModel,
    speechRecognizer: SpeechRecognizer,
    onSummarizeClicked: (String) -> Unit = {}
) {
    val prompt  by viewModel.prompt.collectAsState()
    val response by viewModel.response.collectAsState()
    val focusManager = LocalFocusManager.current
    var color = Color.Gray
    val speakText = stringResource(id = R.string.speak_text)
    val disabled = prompt.isBlank() ||  prompt == speakText
    if(!disabled){
        color = Color.Green
    }

    Column(
        modifier = Modifier
            .padding(all = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = stringResource(id = R.string.gemini_output_label), fontSize = 24.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp))
        TextField(value = response, onValueChange ={} , modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .border(
                2.dp,
                Color.Black, RectangleShape
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            TextField(
                value = prompt,
                label = { Text(stringResource(R.string.summarize_label)) },
                onValueChange = { viewModel.setPrompt(it)},
                modifier = Modifier
                    .weight(6.5f)
                    .border(
                        2.dp,
                        Color.Black, RectangleShape
                    ),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

            )

            TextButton(
                onClick = {
                    if (prompt.isNotBlank()) {
                        onSummarizeClicked(prompt)
                        viewModel.setPrompt("")
                        focusManager.clearFocus(true)
                    }
                },

                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f)
                    .padding(all = 4.dp)
                    .align(Alignment.CenterVertically)
                    .border(
                        2.dp,
                        Color.Black, RectangleShape
                    )
                    .background(color = color, RectangleShape),
                enabled = !disabled


            ) {
                Text(stringResource(R.string.action_go))
            }

            Image(
                painter = painterResource(id  = R.drawable.mic),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(all = 4.dp)
                    .weight(1.5f)
                    .align(Alignment.CenterVertically)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                viewModel.setPrompt(speakText)
                                val speechRecognizerIntent =
                                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                speechRecognizerIntent.putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                );
                                speechRecognizerIntent.putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE,
                                    Locale.getDefault()
                                );
                                speechRecognizer.startListening(speechRecognizerIntent)
                                true
                            }

                            MotionEvent.ACTION_UP -> {
                                viewModel.setPrompt("")
                                true
                            }
                            else -> false
                        }
                    })

        }
    }
}
