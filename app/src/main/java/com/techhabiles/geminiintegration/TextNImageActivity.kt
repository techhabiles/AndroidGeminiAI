package com.techhabiles.geminiintegration

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.techhabiles.geminiintegration.BuildConfig
import com.techhabiles.geminiintegration.R
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

/**
 * TextNImage activity to describe images by Gemini AI
 * @author TechHabiles
 */
class TextNImageActivity : BaseActivity() {
    private lateinit var viewModel: GeminiViewModel
    private lateinit var tts: TextToSpeech
    private var ttsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Gemini pro-vision model to it can take images and describe them
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = BuildConfig.apiKey
        )
        tts = TextToSpeech(this) {
            if( it == TextToSpeech.SUCCESS){
                ttsInitialized = true
                tts.language = Locale.US
            }
            if(it == TextToSpeech.STOPPED){
                viewModel.speakText()
            }
        }
        viewModel = GeminiViewModel(generativeModel)
        lifecycleScope.launch {
            viewModel.speak.collect{
                it?.let{

                    if(it && ttsInitialized){
                        tts.speak(viewModel.response.value, TextToSpeech.QUEUE_FLUSH, null, null)
                    }else{
                        tts.stop()
                    }
                }
            }
        }

    }

    override fun getDataViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
    @Composable
    override fun ScreenContent() {
        TextNImageScreen(viewModel)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextNImageScreen(
    viewModel: GeminiViewModel
) {

    val response by viewModel.response.collectAsState()
    val speaking by viewModel.speak.collectAsState()
    val context = LocalContext.current

    val speakDisabled = response.isBlank()
    var speakColor = Color.Gray
    if(!speakDisabled){
        speakColor = colorResource(id = R.color.th_custom)
    }

    val file = context.createTempImage()
    // URI to store camera image
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri

        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let{
                capturedImageUri = it
            } 
           

        }

    // As permission needed only for Camera, on permission grand invoke camera

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            cameraLauncher.launch(uri)
        }
    }


    Column(
        modifier = Modifier
            .padding(all = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row() {
            Text(
                text = stringResource(id = R.string.gemini_output_label),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )
            TextButton(
                onClick = {
                    viewModel.speakText()
                },

                modifier = Modifier
                    .fillMaxHeight()
                    .padding(all = 4.dp)
                    .align(Alignment.CenterVertically)
                    .border(
                        2.dp,
                        Color.Black, RectangleShape
                    )
                    .background(color = speakColor, RectangleShape),
                // enabled = !speakDisabled


            ) {

                if(speaking) {
                    Text(text = stringResource(id = R.string.action_stop), color = Color.White)
                }else{
                    Text(text = stringResource(id = R.string.action_speak), color = Color.White)

                }
            }

        }
        TextField(value = response, onValueChange = {}, modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .border(
                2.dp,
                Color.Black, RectangleShape
            ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent))

        // if UI have path, show selected or taken image with describe and clear button
        if (capturedImageUri.path?.isNotEmpty() == true) {
              Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .width(150.dp)
                        .height(150.dp)
                        .weight(.5f),
                    painter = rememberAsyncImagePainter(model = capturedImageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            val stream = context.contentResolver.openInputStream(capturedImageUri)
                            val options = BitmapFactory.Options()
                            options.inSampleSize = 8
                            val bitmap = BitmapFactory.decodeStream(stream, null, options)
                            // Create bitmap with smaller size so take less bandwidth on network
                            val input = content {
                                bitmap?.let {
                                    image(it)
                                }
                                text("Describe this image")
                            }
                            viewModel.describeImage(input)
                        },
                        modifier = Modifier
                            .padding(all = 10.dp)
                            .width(200.dp)
                            .border(
                                2.dp,
                                Color.Black, RectangleShape
                            )
                            .background(
                                color = colorResource(id = R.color.th_custom),
                                RectangleShape
                            )


                    ) {
                        Text(stringResource(R.string.describe_label), color = Color.White)
                    }
                    TextButton(
                        onClick = {
                            capturedImageUri = Uri.EMPTY
                            viewModel.clearResponse()
                        },
                        modifier = Modifier
                            .padding(all = 10.dp)
                            .border(
                                2.dp,
                                Color.Black, RectangleShape
                            )
                            .width(200.dp)
                            .background(
                                color = colorResource(id = R.color.th_custom),
                                RectangleShape
                            )


                    ) {
                        Text(stringResource(R.string.clear_label), color = Color.White)
                    }
                }

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            TextButton(
                onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA
                        )
                    // If permission granted, start camera else ask for camera permission
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        // Request a permission
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                },

                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(all = 10.dp)
                    .align(Alignment.CenterVertically)
                    .border(
                        2.dp,
                        Color.Black, RectangleShape
                    )
                    .background(color = colorResource(id = R.color.th_custom), RectangleShape)


            ) {
                Text(stringResource(R.string.action_camera), color = Color.White)
            }

            TextButton(
                onClick = {
                    galleryLauncher.launch("image/*")
                },

                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(all = 10.dp)
                    .align(Alignment.CenterVertically)
                    .border(
                        2.dp,
                        Color.Black, RectangleShape
                    )
                    .background(color = colorResource(id = R.color.th_custom), RectangleShape)


            ) {
                Text(stringResource(R.string.action_gallery), color = Color.White)
            }
        }
    }
}


fun Context.createTempImage(): File {
    // Create an image file with same name for a particular month to avoid multiple file creation
    val timeStamp = SimpleDateFormat("yyyyMM").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}