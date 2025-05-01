package com.mlab.knockme.core.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.net.toUri
import com.ibm.icu.text.RuleBasedNumberFormat
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class ButtonState { Pressed, Idle }
fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.85f else 1f,
        label = ""
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Long.toDateTime(): String{
    var date = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(this)
    val day = SimpleDateFormat("dd", Locale.US).format(System.currentTimeMillis())
    if (day.toInt() == date.split(" ")[0].toInt())
        date = date.split(", ")[1]
    return date.toString()
}

fun Long.toDayPassed(): String{
    val today = System.currentTimeMillis()
    val msDiff = today - this
    val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff)
    return "$daysDiff Days Ago"
}

fun String.toTeacherInitial(): String{
    var initial=""
    val l = this.split(" ")

    l.drop(1).forEach {nm ->
        if(nm.isNotEmpty())
            initial+=nm[0]
    }
    return initial
}

fun String.toShortSemester(): String{
    var shortSem=this
    val l = this.split(", ")
    if(l.size>1 && !l[1].hasAlphabet())
        shortSem = "${l[0]}'${l[1].toInt()%100}"
    return shortSem
}

fun Int.toWords(): String {
    val formatter = RuleBasedNumberFormat(
        Locale.US,
        RuleBasedNumberFormat.SPELLOUT
    )

//    val formatter = MessageFormat(
//        "{0,spellout,currency}",
//        Locale("en", "US")
//    )
    return formatter.format(this).uppercase()
}
fun Double.toK(): String {
    val tk = this/1000
    return "%.1f".format(tk)+"K"
}

infix fun <T> List<T>.equalsIgnoreOrder(other: List<T>) = this.size == other.size && this.toSet() == other.toSet()
infix fun <T> List<T>.notEqualsIgnoreOrder(other: List<T>) = this.size != other.size || this.toSet() != other.toSet()

fun String.hasAlphabet(): Boolean {
    return this.matches("^[a-zA-Z1-9]*$".toRegex())
}

fun Boolean?.isTrue(block: () -> Unit) {
    if (this == true)
        block.invoke()
}
fun <T> List<T>.isNotEmpty(block: (List<T>) -> Unit) {
    if (!isEmpty())
        block.invoke(this)
}
fun <T> List<T>.isEmpty(block: (List<T>) -> Unit) {
    if (isEmpty())
        block.invoke(this)
}

fun String.isNotEmpty(block: (String) -> Unit) {
    if (!isEmpty())
        block.invoke(this)
}

fun Context?.getClipBoardData(): String {
    this ?: return ""
    val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var data = ""
    if (clipBoardManager.primaryClip?.description?.hasMimeType("text/*") == true) {
        clipBoardManager.primaryClip?.itemCount?.let {
            for (i in 0 until it) {
                data += clipBoardManager.primaryClip?.getItemAt(i)?.text ?: ""
            }
        }
    }
    data.log("getClipBoardData")
    return data
}

fun Context.setClipBoardData(data: String?) {
    data ?: return
    val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(data, data)
    clipBoardManager.setPrimaryClip(clip)
    toast("Data Copied!")
}

fun Context?.toast(msg: String?) {
    if (msg.isNullOrEmpty()) return
    MainScope().launch {
        Toast.makeText(this@toast, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Any?.log(tag: String = "TAG"): Any? {
    Log.i("log> '$tag'", "$tag - $this : ${this?.javaClass?.name?.split('.')?.lastOrNull() ?: ""}")
    return this
}

inline fun <T> tryGet(data: () -> T): T? =
    try {
        data()
    } catch (e: Exception) {
        null
    }

fun Context?.showCustomTab(url: String?) {
    runCatching { if (this != null && !url.isNullOrEmpty()) {
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(this, url.toUri())
    } }
}


