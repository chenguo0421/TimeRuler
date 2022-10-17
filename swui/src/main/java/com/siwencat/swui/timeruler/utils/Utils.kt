package com.siwencat.swui.timeruler.utils

import android.content.Context
import android.view.MotionEvent
import java.lang.Exception
import java.util.*
import kotlin.math.sqrt

/**
 * @Description 工具类
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 09-29-2022 周四 15:39
 */

/**
 * dp 转 px
 */
fun dp2px(context: Context, dp: Float): Int =
    (context.resources.displayMetrics.density * dp + 0.5f).toInt()

/**
 * px 转 dp
 */
fun px2dp(context: Context, pixel: Float): Int =
    (pixel / context.resources.displayMetrics.density + 0.5f).toInt()

/**
 * sp 转 px
 */
fun sp2px(context: Context, sp: Float): Int =
    (sp * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()

/**
 * px 转 sp
 */
fun px2sp(context: Context, px: Float): Int =
    (px / context.resources.displayMetrics.scaledDensity + 0.5f).toInt()


fun timeTo24H(paramInt: Int): Int {
    var paramInt = paramInt
    while (paramInt >= 24) {
        paramInt -= 24
    }
    return paramInt
}

fun getStringFormat(format: String?, vararg args: Any?): String {
    return String.format(Locale.US, format!!, *args)
}

fun spacing(event: MotionEvent): Float {
    var x1:Float = 0f
    var y1:Float = 0f
    try {
        x1 = event.getX(1)
        y1 = event.getY(1)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    val x = event.getX(0) - x1
    val y = event.getY(0) - y1
    return sqrt((x * x + y * y).toDouble()).toFloat()
}