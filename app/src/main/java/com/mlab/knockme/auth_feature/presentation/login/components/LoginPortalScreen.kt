package com.mlab.knockme.auth_feature.presentation.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mlab.knockme.core.util.Constants
import com.mlab.knockme.core.util.bounceClick
import com.mlab.knockme.main_feature.presentation.profile.ReportProblem
import com.mlab.knockme.ui.theme.*

@Composable
fun LoginPortalScreen(onClick:(id:String,pass:String)->Unit) {
    val context = LocalContext.current
    var id by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var hidden by rememberSaveable { mutableStateOf(true) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DeepBlue)
    ){
        Column(
            modifier = Modifier
                .padding(horizontal = 50.dp),
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
                    colors = textFieldColors(),
                    modifier = Modifier.fillMaxWidth().semantics { contentType = ContentType.Username }
                )
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = {Text("Input Portal Password")},
                    singleLine = true,
                    colors = textFieldColors(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().semantics { contentType = ContentType.Password },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                )
                Button(
                    modifier = Modifier
                        .padding(top = 70.dp)
                        .fillMaxWidth()
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
//            link = Constants.TERMS_URL
//        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier= Modifier.align(Alignment.BottomCenter)
        ){
            NbNote(
                text = "The application is secure because it's open source on ",
                linkText = "GitHub",
                link = Constants.KNOCK_ME_GIT_URL
            )
            ReportProblem(
                context = context,
                myId = id.ifEmpty { "xxx-xx-xxxx" },
                modifier= Modifier
                    .padding(20.dp)
                    .padding(bottom = 40.dp),
                color = TextBlue.copy(.7f)
            )
        }

//        if(!hidden){
//
//            LoadingScreen(msg)
//        }
    }
}

@Composable
fun TitlePortal() {
    Spacer(modifier = Modifier.size(20.dp))
    Text(
        text = "STUDENT\nPORTAL",
        style = MaterialTheme.typography.headlineLarge,
        fontSize = 45.sp,
        lineHeight = 50.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 50.dp)
    )
    
}
@Composable
fun textFieldColors() =
    TextFieldDefaults.colors(
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
    Column(
        modifier = modifier
            .padding(vertical = 20.dp, horizontal = 70.dp),
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
//                        context.showCustomTab(link)
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