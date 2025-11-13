package com.example.guideme.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guideme.ui.theme.GuideMeTheme
import com.example.guideme.ui.theme.MainBackgroundGradient
import com.example.guideme.ui.theme.MainButtonColor
import com.example.guideme.ui.theme.MainButtonContentColor
import me.nikhilchaudhari.library.neumorphic
import me.nikhilchaudhari.library.shapes.Pressed

@Composable
fun SkeuomorphicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 25.dp,
    backgroundColor: Color = MainButtonColor,
    textColor: Color = MainButtonContentColor,
    elevation: Dp = 10.dp,
    width: Dp? = null,            // optional fixed width
    height: Dp = 56.dp,           // default height
    fontSize: TextUnit = 18.sp,
) {
    Box(
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)

            // Outer shadow for raised effect

            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color(0x33000000),
                spotColor = Color(0x22000000)
            )
            // Light top-left highlight

            .background(backgroundColor)

            .drawBehind {
                // subtle light top-left
                val highlightColor = Color.White.copy(alpha = 0.3f)
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(highlightColor, Color.Transparent)
                    ),
                    size = size
                )
            }
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp))
            .neumorphic(neuShape = Pressed.Rounded(20.dp))


        ,
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TesterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = MainButtonColor,
    textColor: Color = MainButtonContentColor,
    elevation: Dp = 10.dp,
    width: Dp = 30.dp,            // optional fixed width
    height: Dp = 56.dp,           // default height
    fontSize: TextUnit = 18.sp,
) {
    Box(
        modifier = Modifier
            .neumorphic()
            .width(width = width)
            .height(height),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SkeuomorphicButtonPreview() {
    GuideMeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .background(MainBackgroundGradient),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ){
                SkeuomorphicButton(
                    text = "Login",
                    onClick = {}
                )
                SkeuomorphicButton(
                    text = "Register",
                    onClick = {},
                    backgroundColor = Color(0xFFEDE8F3), // slightly different shade
                    textColor = MainButtonContentColor
                )
                SkeuomorphicButton(
                    text = "Skip",
                    onClick = {},
                    width = 150.dp,   // custom width
                    height = 40.dp,   // custom height
                    fontSize = 14.sp
                )
                TesterButton(
                    text = "Skip",
                    onClick = {},
                    // custom width

                    height = 50.dp,   // custom height
                    width = 150.dp,
                    fontSize = 14.sp
                )

            }}
    }
}
