package com.mlab.knockme.main_feature.presentation.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mlab.knockme.auth_feature.domain.model.SemesterInfo
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.main_feature.presentation.main.TopBar
import com.mlab.knockme.profile_feature.presentation.components.standardQuadFromTo
import com.mlab.knockme.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaViewScreen(navController: NavHostController) {
    val lst = listOf(
        SemesterInfo("","Summer",2023,3.45,15.0),
        SemesterInfo("","Fall",2023,2.45,9.0),
        SemesterInfo("","Spring",2022,3.55,22.0),
        SemesterInfo("","Summer",2023,1.45,15.0),
        SemesterInfo("","Fall",2019,3.45,15.0),
        SemesterInfo("","Summer",2023,3.75,15.0),
        SemesterInfo("","Summer",2023,3.45,15.0),

        )
    Scaffold(topBar = {TopBar(navController)}) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(it)
            .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "Congratulation,",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 5.dp)
                    .padding(top=10.dp),
                //textAlign = TextAlign.End
            )
            Text(
                text =
                if(lst.size>1)
                    "You have completed ${lst.size} semesters\nand ${lst.sumOf {sem -> sem.creditTaken.toInt()}} credits."
                else
                    "You completed ${lst.size} semester.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextWhite,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                //textAlign = TextAlign.End
            )
            Text(
                text = "All SGPAs",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(top = 20.dp, bottom = 10.dp),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 5.dp, end = 5.dp, bottom = 100.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(lst.size) {ind->
                    if(lst[ind].sgpa < 3.0)
                        SemesterInfoItem(lst[ind], Beige1, Beige2, Beige3)
                    else if(lst[ind].sgpa >= 3.7)
                        SemesterInfoItem(lst[ind], LightGreen1, LightGreen2, LightGreen3)
                    else if(lst[ind].sgpa >= 3.5)
                        SemesterInfoItem(lst[ind], BlueViolet1, BlueViolet2, BlueViolet3)
                    else
                        SemesterInfoItem(lst[ind], Limerick1, Limerick2, Limerick3)
                }
            }
            LastUpdated(63487325)
        }
    }
}

@Composable
fun SemesterInfoItem(
    semesterInfo: SemesterInfo,
    lightColor: Color = Limerick1,
    mediumColor: Color = Limerick2,
    darkColor: Color = Limerick3
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(5.dp)
            .aspectRatio(1f)
            .bounceClick()
            .clip(RoundedCornerShape(10.dp))
            .clickable{

            }
            .background(darkColor)
    ) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight+50

        // Medium colored path
        val mediumColoredPoint1 = Offset(0f, height * 0.3f)
        val mediumColoredPoint2 = Offset(width * 0.1f, height * 0.35f)
        val mediumColoredPoint3 = Offset(width * 0.4f, height * 0.15f)
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
        val lightPoint4 = Offset(width * 0.65f, height * 0.85f)
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
        Text(
            text = semesterInfo.sgpa.toString(),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .align(Alignment.Center)
        )
        Text(
            text = semesterInfo.toShortSemName(),
            style = MaterialTheme.typography.bodySmall,
            color = TextWhite,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
        Text(
            text = "${semesterInfo.creditTaken.toInt()} Credits",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp,
            color = TextWhite,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Previews1() {
    KnockMETheme {
        CgpaViewScreen(rememberNavController())
    }
}