package com.mlab.knockme.auth_feature.presentation.login.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mlab.knockme.ui.theme.*

@Composable
fun LoadingScreen( data: String

) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        CircularProgressIndicator(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(15.dp)),
            color = BlueViolet3,
            strokeWidth = 15.dp,
            trackColor = DeepBlueLess,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(50.dp))
        LoadingText(data)
        //CircularProgressbar3()
    }

}

@Composable
fun LoadingText(data: String) {
    Text(
        modifier = Modifier
            .padding(50.dp)
            .height(150.dp),
        textAlign = TextAlign.Center,
        text = data,
        style = MaterialTheme.typography.headlineMedium,
        color=TextBlue
    )
}

@Composable
fun CircularProgressbar3(
    number: Float = 70f,
    numberStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    size: Dp = 180.dp,
    indicatorThickness: Dp = 28.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0,
    foregroundIndicatorColor: Color = Color(0xFF35898f),
    backgroundIndicatorColor: Color = Color.LightGray.copy(alpha = 0.3f)
) {

    // It remembers the number value
    var numberR by remember {
        mutableStateOf(0f)
    }

    // Number Animation
    val animateNumber = animateFloatAsState(
        targetValue = numberR,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        ), label = "animate"
    )

    // This is to start the animation when the activity is opened
    LaunchedEffect(Unit) {
        numberR = number
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size = size)
    ) {
        Canvas(
            modifier = Modifier
                .size(size = size)
        ) {

            // Background circle
            drawCircle(
                color = backgroundIndicatorColor,
                radius = size.toPx() / 2,
                style = Stroke(width = indicatorThickness.toPx(), cap = StrokeCap.Round)
            )

            val sweepAngle = (animateNumber.value / 100) * 360

            // Foreground circle
            drawArc(
                color = foregroundIndicatorColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(indicatorThickness.toPx(), cap = StrokeCap.Round)
            )
        }

        // Text that shows number inside the circle
        Text(
            text = (animateNumber.value).toInt().toString(),
            style = numberStyle
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

}


@Preview(showBackground = true)
@Composable
fun Pre() {
	KnockMETheme {
        LoadingScreen("hi")
    }
}