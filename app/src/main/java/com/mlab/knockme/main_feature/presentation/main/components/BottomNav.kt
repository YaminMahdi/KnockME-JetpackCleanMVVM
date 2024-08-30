package com.mlab.knockme.main_feature.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.main_feature.presentation.NavItems
import com.mlab.knockme.ui.theme.*

@Composable
fun BottomNav(row: @Composable ()-> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBlueLess)
            .padding(vertical = 10.dp, horizontal = 5.dp),
        
    ) {
        row.invoke()
    }
}

@Composable
fun BottomMenuItem(
    item: NavItems,
    isSelected: Boolean = false,
    onItemClick: () -> Unit
) {
    val activeHighlightColor: Color = ButtonBlue
    val activeTextColor: Color = Color.White
    val inactiveTextColor: Color = AquaBlue
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = if(isSelected) Modifier.bounceClick() else Modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable{
                        onItemClick.invoke()
                }
                .background(if (isSelected) activeHighlightColor else Color.Transparent)
                .padding(10.dp)
        ) {
            Icon(
                //item.icon,
                painter =  painterResource(id = item.iconId),
                contentDescription = item.title,
                tint = if (isSelected) activeTextColor else inactiveTextColor,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 3.dp))
        Text(
            text = item.title,
            color = if(isSelected) activeTextColor else inactiveTextColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewsX() {
    KnockMETheme {
        BottomNav {

        }
    }
}
