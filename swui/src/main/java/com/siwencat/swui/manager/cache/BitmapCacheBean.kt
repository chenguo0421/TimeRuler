package com.siwencat.swui.manager.cache

import android.graphics.Bitmap

/**
 * @Description 录像切片图片的内存缓存实体
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 10-11-2022 周二 17:53
 */
data class BitmapCacheBean(
    var time: Int,
    var devId: String,
    var channelIndex: Int,
    var streamType:Int,
    var bitmap: Bitmap,
    var isLongPressedFrame:Boolean = false
) {

}