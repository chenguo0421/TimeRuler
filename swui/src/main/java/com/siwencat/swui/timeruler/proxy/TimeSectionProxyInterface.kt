package com.siwencat.swui.timeruler.proxy

import com.siwencat.swui.manager.cache.BitmapCacheBean
import com.siwencat.swui.timeruler.bean.FrameBitmap
import com.siwencat.swui.timeruler.bean.ScaleType
import com.siwencat.swui.timeruler.bean.VideoClip

/**
 * @Description 时间轴操作相关的接口定义
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 09-29-2022 周四 16:27
 */
interface TimeSectionProxyInterface {

    /**
     * 设置录像片段到时间切片控件
     * 设置录像片段后，事件切片控件才会根据录像片段集合，在时间轴上绘制不同录像类型的填充颜色
     * @param videoClips 录像片段集合
     */
    fun setVideoClip(videoClips: List<VideoClip>)

    /**
     * 设置缩略图
     * 缩略图集合中的每个项必须包括Bitmap，帧索引，缩略图帧在时间轴的时间偏移，图片宽度和高度
     * @param bitmapCacheBean 录像切片
     */
    fun setThumbnails(bitmapCacheBean: BitmapCacheBean)

    /**
     * 设置时间偏移，单位为秒， 当两次设置时间相同时，时间轴不渲染，不响应
     * 时间偏移指的是当天以秒为单位的时间偏移量，例如时间偏移为 1 * 60 * 60时，时间轴的时间线指向的是01:00:00刻度
     * @param timeOffset 时间偏移，从0开始，最大为 24 * 60 * 60秒
     */
    fun setTimeOffset(timeOffset: Int)

    /**
     * 获取时间轴的时间偏移
     * 时间偏移指的是当天以秒为单位的时间偏移量，例如时间轴的时间线指向的是01:00:00刻度时，时间偏移为 1 * 60 * 60
     * @return timeOffset 时间偏移，从0开始，最大为 24 * 60 * 60秒
     */
    fun getTimeOffset(): Int

    /**
     * 设置当天的0时0分0秒的时间戳，单位为秒
     * 当[dayStartTime]发生变化时，需要清空时间轴的录像片段集合，否则切换日期后，若切换的日期没有录像片段，则时间轴还是展示的上一次的录像片段集合
     *
     * @param dayStartTime 当天的0时0分0秒的时间戳，单位为秒
     */
    fun setCurrentDayStartTime(dayStartTime: Int)

    /**
     * 设置回放设备的信息
     *
     * @param devId 设备id
     * @param channelIndex 通道索引
     * @param streamType 流类型
     */
    fun setDeviceInfo(devId: String,channelIndex:Int,streamType:Int)


    /**
     * 设置时间切片控件对外的回调
     * 例如时间轴拖拽事件，
     * @param listener 时间切片控件对外的回调
     */
    fun setTimeSectionListener(listener: TimeSectionListener)

}