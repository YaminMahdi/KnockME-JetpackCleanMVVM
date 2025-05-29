@file:Suppress("unused")

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.round

enum class ButtonState { Pressed, Idle }
fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.90f else 1f,
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


fun <T> T.isNull() = this == null
fun <T> T.isNotNull() = this != null

inline fun <T> T?.isNull(next: () -> Unit): T? {
    if (this == null) next()
    return this
}

inline fun <T> T?.isNotNull(next: (T) -> Unit): T? {
    if (this != null) next(this)
    return this
}

fun Int?.isNullOrZero() = this == null || this == 0
fun Double?.toOneIfZero() = if (this == 0.0 || this == null) 1.0 else this
fun String?.toNAifEmpty() =
    if (isNullOrEmpty() || isBlank() || (this == "0" || this == "0.0")) "N/A" else this

fun String?.toNullifEmpty() =
    if (isNullOrEmpty() || isBlank() || (this == "0" || this == "0.0")) null else this

fun String?.toDoubleOrZero() =
    this?.replace("[^\\d.]".toRegex(), "")?.toDoubleOrNull().orZero().roundTo(2)

fun Int?.orMinusOne() = this ?: -1
fun Int?.orZero() = this ?: 0
fun Long?.orZero() = this ?: 0L
fun Double?.orZero() = this ?: 0.0
fun Float?.orZero() = this ?: 0.0F
fun String?.orZero() = this?.toIntOrNull() ?: 0
fun String?.orZeroD() = this?.toDoubleOrNull() ?: 0.0
fun Boolean?.orFalse() = this == true
fun Boolean.orNull() = if (!this) null else true

fun Int?.isZero() = this == null || this == 0
fun Int?.isMinusOne() = this == null || this == -1
fun Long?.isZero() = this == null || this == 0L
fun Double?.isZero() = this == null || this == 0.0
fun Float?.isZero() = this == null || this == 0.0F
fun Float?.isMinusOne() = this == null || this == -1.0F
fun Float?.isZeroOrMinusOne() = this == null || this == 0.0F || this == -1.0F
fun String?.isZero() = this == null || this.toIntOrNull() == 0
fun String?.isZeroD() = this == null || this.toDoubleOrNull() == 0.0

fun Number.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(toDouble() * factor) / factor
}

inline fun <T> tryInMain(crossinline data: suspend CoroutineScope.() -> T) =
    CoroutineScope(Dispatchers.Main.immediate).launch {
        try {
            data()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


