package com.mlab.knockme.main_feature.presentation.profile

import com.mlab.knockme.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.core.util.toDayPassed
import com.mlab.knockme.main_feature.presentation.main.TopBar
import com.mlab.knockme.profile_feature.presentation.components.standardQuadFromTo
import com.mlab.knockme.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DueViewScreen(navController: NavHostController) {
    Scaffold(topBar = {TopBar(navController)}) {
        Column(modifier = Modifier
            .padding(it)
            .background(DeepBlue)
            .fillMaxSize()
            .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Payment Info",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
            PaymentInfoItem(
                "Total Payable", 100434.0,
                BlueViolet1, BlueViolet2, BlueViolet3 )
            PaymentInfoItem(
                "Total Paid", 100434.0,
                LightGreen1, LightGreen2, LightGreen3 )
            PaymentInfoItem(
                "Total Due", 100434.0,
                Beige1, Beige2, Beige3 )
            PaymentInfoItem(
                "Total Other", 100434.0,
                Limerick1, Limerick2, Limerick3 )
            LastUpdated(63478563485)

        }
    }
}

@Composable
fun LastUpdated(lastUpdated: Long?) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(.7f)
            .padding(5.dp),
        text = "Last Updated: ${lastUpdated?.toDayPassed()}",
        fontSize = 10.sp,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.End
    )
}

@Composable
fun PaymentInfoItem(
    type: String,
    taha: Double,
    lightColor: Color,
    mediumColor: Color,
    darkColor: Color
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(vertical = 7.5.dp)
            .aspectRatio(2.8f)
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .clickable {

            }
            .background(darkColor)
    ) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        // Medium colored path
        val mediumColoredPoint1 = Offset(0f, height * 0.3f)
        val mediumColoredPoint2 = Offset(width * 0.1f, height * 0.35f)
        val mediumColoredPoint3 = Offset(width * 0.4f, height * 0.05f)
        val mediumColoredPoint4 = Offset(width * 0.75f, height * 0.7f)
        val mediumColoredPoint5 = Offset(width * 1.4f, -height.toFloat())

        val mediumColoredPath = Path().apply {
            moveTo(mediumColoredPoint1.x, mediumColoredPoint1.y)
            standardQuadFromTo(mediumColoredPoint1, mediumColoredPoint2)
            standardQuadFromTo(mediumColoredPoint2, mediumColoredPoint3)
            standardQuadFromTo(mediumColoredPoint3, mediumColoredPoint4)
            standardQuadFromTo(mediumColoredPoint4, mediumColoredPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }

        // Light colored path
        val lightPoint1 = Offset(0f, height * 0.35f)
        val lightPoint2 = Offset(width * 0.1f, height * 0.4f)
        val lightPoint3 = Offset(width * 0.3f, height * 0.35f)
        val lightPoint4 = Offset(width * 0.65f, height.toFloat())
        val lightPoint5 = Offset(width * 1.4f, -height.toFloat() / 3f)

        val lightColoredPath = Path().apply {
            moveTo(lightPoint1.x, lightPoint1.y)
            standardQuadFromTo(lightPoint1, lightPoint2)
            standardQuadFromTo(lightPoint2, lightPoint3)
            standardQuadFromTo(lightPoint3, lightPoint4)
            standardQuadFromTo(lightPoint4, lightPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            drawPath(
                path = mediumColoredPath,
                color = mediumColor
            )
            drawPath(
                path = lightColoredPath,
                color = lightColor
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Text(
                text = "৳$taha",
                style = MaterialTheme.typography.headlineLarge,
                fontSize= 32.sp,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                text = type,
                color = TextWhite,
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
        Image(
            painter = painterResource(id = R.drawable.bdt),
            contentDescription = "taha",
            modifier = Modifier
                .width(120.dp)
                .padding(12.dp)
                .alpha(.07f)
                .align(Alignment.BottomStart)
        )

    }
    
}

@Preview(showBackground = true)
@Composable
fun Previews2() {
    KnockMETheme {
        DueViewScreen(rememberNavController())
    }
}