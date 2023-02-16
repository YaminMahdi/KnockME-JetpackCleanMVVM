package com.mlab.knockme.auth_feature.presentation.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPortalScreen(onClick:(id:String,pass:String)->Unit) {

    var hidden by remember{ mutableStateOf(true) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DeepBlue)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
                ){
            TitlePortal()
            var id by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            //var passwordHidden by rememberSaveable { mutableStateOf(true) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    label = {Text("ID")},
                    placeholder = {Text("Input Student ID")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = {Text("Input Portal Password")},
                    singleLine = true,
                    colors = textFieldColors(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )
                Button(
                    modifier = Modifier
                        .padding(top=70.dp)
                        .bounceClick()
                    ,
                    onClick = {
                        hidden = false
                        onClick.invoke(id,password)
                    },
                    shape= RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor= ButtonBlue)
                ) {
                    Text(
                        text = "VERIFY",
                        color = TextWhite,
                        fontFamily = ubuntu
                    )
                }
            }
        }
        NbNote(modifier= Modifier.align(Alignment.BottomCenter))
        Spacer(modifier = Modifier.padding(20.dp))
//        if(!hidden){
//
//            LoadingScreen(msg)
//        }
    }
}

@Composable
fun TitlePortal() {
    Spacer(modifier = Modifier.padding(20.dp))
    Text(
        text = "STUDENT PORTAL",
        style = MaterialTheme.typography.headlineLarge,
        fontSize = 45.sp,
        lineHeight = 50.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(50.dp)
    )
    
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textFieldColors() =
    TextFieldDefaults.textFieldColors(
    textColor = DeepBlueLess,
    focusedLabelColor = LightGreen2,
    unfocusedLabelColor= LightGreen2,
    focusedIndicatorColor = LightGreen2,
    unfocusedIndicatorColor = LightGreen2,
    containerColor = DeepBlueLess,
    cursorColor = AquaBlue,
    placeholderColor = DeepBlueLess
)


@Composable
fun NbNote(
    modifier: Modifier = Modifier,
    text: String= "The application is secure because it's open source."
) {
    Column(
        modifier = modifier
            .padding(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "N.B.",
            style = MaterialTheme.typography.headlineSmall,
            color = LightGreen2
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun LoginPortalView() {
    KnockMETheme {
        //LoginPortalScreen(onClick = { s: String, s1: String -> })
    }
}