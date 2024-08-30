package com.mlab.knockme.auth_feature.presentation.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.main_feature.presentation.profile.ReportProblem
import com.mlab.knockme.ui.theme.AquaBlue
import com.mlab.knockme.ui.theme.BlueViolet3
import com.mlab.knockme.ui.theme.ButtonBlue
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.DeepBlueLess
import com.mlab.knockme.ui.theme.KnockMETheme
import com.mlab.knockme.ui.theme.LightBlue
import com.mlab.knockme.ui.theme.TextBlue
import com.mlab.knockme.ui.theme.TextWhite
import com.mlab.knockme.ui.theme.ubuntu

@Composable
fun LoginPortalScreen(onClick:(id:String,pass:String)->Unit) {
    val context = LocalContext.current
    var id by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var hidden by rememberSaveable{ mutableStateOf(true) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DeepBlue)
    ){

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
                ){
            TitlePortal()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val fm= LocalFocusManager.current
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
                        .padding(top = 70.dp)
                        .bounceClick()
                    ,
                    onClick = {
                        fm.clearFocus()
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
//        NbNote(
//            text = "By signing in you are agreeing with our ",
//            modifier= Modifier.align(Alignment.BottomCenter),
//            linkText = "Terms and Condition",
//            link = "https://knock-me.github.io/terms.htm"
//        )
        NbNote(
            text = "The application is secure because it's open source on ",
            modifier= Modifier.align(Alignment.BottomCenter),
            linkText = "GitHub",
            link = "https://github.com/YaminMahdi/KnockME-JetpackCleanMVVM"
        )
        ReportProblem(
            context = context,
            myId = id.ifEmpty { "xxx-xx-xxxx" },
            modifier= Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            color = TextBlue.copy(.7f)

        )
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
@Composable
fun textFieldColors() =
    TextFieldDefaults.colors().copy(
        focusedTextColor = TextWhite,
        focusedLabelColor = BlueViolet3,
        unfocusedLabelColor= BlueViolet3,
        focusedIndicatorColor = BlueViolet3,
        unfocusedIndicatorColor = BlueViolet3,
        focusedContainerColor = DeepBlueLess,
        unfocusedContainerColor = DeepBlueLess,
        cursorColor = AquaBlue,
        focusedPlaceholderColor = DeepBlueLess
    )


@Composable
fun NbNote(
    modifier: Modifier = Modifier,
    text: String,
    linkText: String,
    link: String
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier
            .padding(vertical = 80.dp, horizontal = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "N.B.",
            style = MaterialTheme.typography.headlineSmall,
            color = LightBlue
        )
        Spacer(modifier = Modifier.padding(5.dp))
        val annotatedString = buildAnnotatedString {
            append(text)
//            withStyle(style = MaterialTheme.typography.headlineSmall.toSpanStyle().copy(color = LightBlue)) {
//                pushStringAnnotation(tag = linkText, annotation = linkText)
//                append(linkText)
//            }
            withLink(LinkAnnotation.Url(url = link,
                styles = TextLinkStyles(style = SpanStyle(color = LightBlue)))
            ) {
                append(linkText)
            }
        }
//        ClickableText(text = annotatedString, onClick = { offset ->
//            annotatedString.getStringAnnotations(offset, offset)
//                .firstOrNull()?.let { span ->
//                    println("Clicked on ${span.item}")
//                    if(span.item==linkText)
//                        uriHandler.openUri(link)
//                }
//        },
//            style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
//        )
        Text(
            text = annotatedString,
            style = MaterialTheme.typography.headlineSmall
                .copy(textAlign = TextAlign.Center),
        )
    }
}

@Preview
@Composable
fun LoginPortalView() {
    KnockMETheme {
        LoginPortalScreen(onClick = { _: String, _: String -> })
    }
}