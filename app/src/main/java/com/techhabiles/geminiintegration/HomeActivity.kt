package com.example.geminiintegration

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.geminiintegration.util.UiUtils
import com.techhabiles.geminiintegration.R

/**
 *  Home Activity to provide a list of options to the user
 *  @author TechHabiles
 */

class HomeActivity : BaseActivity() {

    @Composable
    override fun ScreenContent() {
        RenderHome()
    }
}

@Composable
fun RenderHome() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val imageModifier = Modifier
            .width(256.dp)
            .height(256.dp)
            .padding(all = 8.dp)
            .clip(shape = CircleShape)
            .border(8.dp, Color.Gray, CircleShape)
            .background(Color.Transparent)
        Image(
            painter = painterResource(id = R.drawable.techhabiles),
            contentDescription = "String",
            contentScale = ContentScale.Fit,
            modifier = imageModifier
        )
        TextButton(
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth()
                .border(
                    2.dp,
                    Color.Black, RectangleShape
                )
                .background(color = colorResource(id =  R.color.th_custom), RectangleShape),

                    onClick = {
                UiUtils.showScreen(context, TextActivity::class.java as Class<ComponentActivity>)
            },
        ) {
            Text(text = " Gemini Text Gen AI", fontSize = 22.sp,  color = Color.White)
        }

        TextButton(
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth()
                .border(
                    2.dp,
                    Color.Black, RectangleShape
                )
                .background(color = colorResource(id =  R.color.th_custom), RectangleShape),

            onClick = {
                UiUtils.showScreen(
                    context,
                    TextNImageActivity::class.java as Class<ComponentActivity>
                )

            },
        ) {
            Text(text = "Gemini Image + Text Gen AI", fontSize = 22.sp,  color = Color.White)
        }
        TextButton(
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth()
                .border(
                    2.dp,
                    Color.Black, RectangleShape
                )
                .background(color = colorResource(id =  R.color.th_custom), RectangleShape),

            onClick = {
                UiUtils.showScreen(
                    context,
                    ChatActivity::class.java as Class<ComponentActivity>
                )

            },
        ) {
            Text(text = "Gemini Multi Turn Conversation", fontSize = 22.sp, color = Color.White)
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun RenderHomePreview() {
    RenderHome()
}