package com.siwencat.swui.timeruler.bean

import androidx.annotation.ColorInt

/**
 * @Description 录像片段实体
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 09-29-2022 周四 14:45
 */
data class VideoClip(
    var startTime: Long,//录像片段开始时间偏移，从当天的0点开始，以秒为单位
    var endTime: Long,//录像片段结束时间偏移，从当天的0点开始，以秒为单位
    var eventType: Int,//录像片段类型
    @ColorInt var clipColor: Int//录像片段类型的填充颜色
)

