package com.siwencat.swui.timeruler.intf

import com.siwencat.swui.timeruler.view.SWUITimeRuler

/**
 * @Description TODO
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 09-29-2022 周四 14:59
 */
internal interface OnDateSeekBarChangeListener {
    fun onProgressChanged(
        dateSeekbar: SWUITimeRuler,
        param1: Float,
        param2: Float,
        timeSeconds: Long,
        pointcount: Int
    )

    fun onStartTrackingTouch(
        dateSeekbar: SWUITimeRuler,
        param1: Float,
        param2: Float
    )

    fun onStopTrackingTouch(
        dateSeekbar: SWUITimeRuler,
        param1: Long,
        param2: Int
    )

    fun onStopZoom(dateSeekbar: SWUITimeRuler, param: Float)
    fun onZooming(dateSeekBar: SWUITimeRuler)
    fun onLongPressed()
    fun moveSplit(moveLeft: Boolean, startTime: Long, endTime: Long)
    fun autoMoveSplit(moveLeft: Boolean, startTime: Long, endTime: Long)
    fun splitLongPressed(time: Long)
    fun onPointerUp()
}