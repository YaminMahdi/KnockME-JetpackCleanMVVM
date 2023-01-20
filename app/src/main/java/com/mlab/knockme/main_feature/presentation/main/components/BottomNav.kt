package com.mlab.knockme.main_feature.presentation.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mlab.knockme.main_feature.presentation.MainScreens
import com.mlab.knockme.ui.theme.*

@Composable
fun BottomNav(row: @Composable ()-> Unit) {
//    val items: List<BottomNavContent> = listOf(
//    BottomNavContent("Personal",R.drawable.ic_chat),        //Icons.Rounded.ShoppingCart
//    BottomNavContent("Places",R.drawable.ic_education),
//    BottomNavContent("Bus Info",R.drawable.ic_bus),
//    BottomNavContent("Profile",R.drawable.ic_profile)
//    )
    val modifier: Modifier = Modifier
    val activeHighlightColor: Color = ButtonBlue
    val activeTextColor: Color = Color.White
    val inactiveTextColor: Color = AquaBlue
    val initialSelectedItemIndex: Int = 0
    var selectedItemIndex by remember {
        mutableStateOf(initialSelectedItemIndex)
    }
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(DeepBlueLess)
            .padding(vertical = 10.dp, horizontal = 5.dp),
        
    ) {
        row.invoke()
//        items.forEachIndexed { index, item ->
//            BottomMenuItem(
//                item = item,
//                isSelected = index == selectedItemIndex,
//                activeHighlightColor = activeHighlightColor,
//                activeTextColor = activeTextColor,
//                inactiveTextColor = inactiveTextColor
//            ) {
//                selectedItemIndex = index
//            }
//        }
    }
}

@Composable
fun BottomMenuItem(
    item: MainScreens,
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
