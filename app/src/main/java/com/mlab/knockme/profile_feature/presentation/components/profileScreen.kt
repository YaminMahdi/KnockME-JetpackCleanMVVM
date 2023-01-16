package com.mlab.knockme.profile_feature.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mlab.knockme.R
import com.mlab.knockme.ui.theme.*

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .background(DeepBlue)
            .fillMaxSize()){
        TitleInfo()
        PersonInfo()
        FeatureSection(features = listOf(
            Feature(
                title = "CGPA",
                info = "3.65" ,
                BlueViolet1,
                BlueViolet2,
                BlueViolet3
            ),
            Feature(
                title = "DUE",
                info = "30.5k",
                Beige1,
                Beige2,
                Beige3

            ),
            Feature(
                title = "Course",
                info = "SIX" ,
                Limerick1,
                Limerick2,
                Limerick3
            ),
            Feature(
                title = "Result",
                info = "LIVE" ,
                LightGreen1,
                LightGreen2,
                LightGreen3
            )
        ))
    }

}

@Composable
fun TitleInfo(
    title: String = "Profile"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontSize = 26.sp
        )
        Row{
            Ic(Icons.TwoTone.Info)
            Ic(Icons.TwoTone.Settings)
        }
    }
}

@Composable
fun Ic(
    iVec: ImageVector = Icons.Rounded.Settings,
    onClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(start = 12.dp)
            .clickable {
                if (onClick != null) {
                    onClick()
                }
            }
            .clip(RoundedCornerShape(18.dp))
            .background(DarkerButtonBlue)
            .padding(8.dp)


    ) {
        Icon(
            iVec,
            contentDescription ="id",
            modifier= Modifier.size(26.dp),
            tint = AquaBlue
        )
    }

}

@Composable
fun PersonInfo(
    name: String = "Ahmad Umar Mahdi",
    pic: String = "",
    program: String = "B.Sc. in CSE",
    id: String = "193-15-1071"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SubcomposeAsyncImage(
            model = pic,
            contentDescription = name,
            modifier = Modifier
                .fillMaxWidth(.4f)
                .aspectRatio(1f)
                .padding(15.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkerButtonBlue)
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        color = AquaBlue,
                        modifier = Modifier
                            .padding(25.dp)
                    )}
                is AsyncImagePainter.State.Error -> {
                    SubcomposeAsyncImageContent(
                        painter = painterResource(id = R.drawable.ic_profile),
                        alpha = .7F
                    )}
                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
//        Image(
//            painter = rememberAsyncImagePainter(pic),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxWidth(.4f)
//                .aspectRatio(1f)
//                .clip(RoundedCornerShape(10.dp))
//        )
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(15.dp)

        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = program,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ID: $id",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }

}
@Composable
fun FeatureSection(features: List<Feature>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Features",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(15.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 7.5.dp, end = 7.5.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(features.size) {
                FeatureItem(feature = features[it])
            }
        }
    }
}

@Composable
fun FeatureItem(
    feature: Feature
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(7.5.dp)
            .aspectRatio(1f)
            .clickable {

            }
            .clip(RoundedCornerShape(20.dp))
            .background(feature.darkColor)
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
                color = feature.mediumColor
            )
            drawPath(
                path = lightColoredPath,
                color = feature.lightColor
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Text(
                text = feature.info,
                color = TextWhite,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(vertical = 6.dp, horizontal = 15.dp)
            )
        }
        Text(
            text = feature.title,
            style = MaterialTheme.typography.headlineMedium,
            lineHeight = 26.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(15.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewsProfile() {
    KnockMETheme {
        ProfileScreen()
    }
}