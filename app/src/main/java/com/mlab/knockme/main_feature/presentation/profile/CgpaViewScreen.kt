package com.mlab.knockme.main_feature.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mlab.knockme.main_feature.presentation.main.TopBar
import com.mlab.knockme.ui.theme.KnockMETheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaViewScreen(navController: NavHostController) {
    Scaffold(topBar = {TopBar(navController)}) {
        Column(modifier = Modifier
            .padding(it)
        ) {


        }
    }
}

@Preview(showBackground = true)
@Composable
fun Previews1() {
    KnockMETheme {
        CgpaViewScreen(rememberNavController())
    }
}