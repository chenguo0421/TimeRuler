package com.siwencat.swui.timeruler.proxy

import android.graphics.Bitmap

/**
 * @Description 时间轴向外
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 09-29-2022 周四 17:20
 */
interface TimeSectionListener {

    /**
     * 时间轴上的时间发生指向变化时，向外抛出当前指向的时间
     *
     * @param offset 当前时间轴的时间偏移量 从0开始，最大为 24 * 60 * 60秒
     */
    @Deprecated("暂时不往外回调该方法，回调出去无意义")
    fun onTimeChange(offset: Int)



    /**
     * 时间轴上的拖拽事件，将拖拽过程中时间轴指向的时间向外抛出
     *
     * @param time 时间轴指针指向的时间,单位为秒
     */
    fun onTimeRulerDrag(time: Int)

    /**
     * 时间轴上的拖拽事件，当拖拽时时间轴指向的时间不在录像片段时间区间之内时，向外抛出时间轴指向的时间
     *
     * @param time 时间轴指针指向的时间,单位为秒
     */
    fun onTimeRulerDragOutOfClips(time: Int)

    /**
     * 时间轴上的拖拽结束事件，将拖拽结束的时间轴指向的时间向外抛出
     *
     * @param time 时间轴指向的时间
     */
    fun onTimeRulerDragStop(time: Int)

    /**
     * 时间轴上的单指点击开始事件
     *
     * @param time 时间轴指向的时间
     */
    fun onTimeRulerClickDown(time: Int)

    /**
     * 时间轴上的点击结束事件
     *
     * @param time 时间轴指向的时间
     */
    fun onTimeRulerClickUp(time: Int)


    /**
     * 时间轴上的拖拽事件，将拖拽过程中时间轴指向的时间向外抛出
     *
     * @param offset 时间轴指向的时间偏移量 从0开始，最大为 24 * 60 * 60秒
     */
    @Deprecated("暂时不往外回调该方法，回调出去无意义")
    fun onTimeRulerZooming(offset: Int)

    /**
     * 时间轴上的拖拽事件，将拖拽过程中时间轴指向的时间向外抛出
     *
     * @param offset 时间轴指向的时间偏移量 从0开始，最大为 24 * 60 * 60秒
     */
    @Deprecated("暂时不往外回调该方法，回调出去无意义")
    fun onTimeRulerZoomStop(offset: Int)


    /**
     * 请求指定时间点的缩略图帧
     *
     * @param devId 设备id
     * @param channelIndex 通道索引
     * @param streamType 流类型
     * @param time 指定的时间点时间戳，单位为秒
     * @return Bitmap
     */
    fun onRequestThumbnailFrame(
        devId: String, channelIndex: Int, streamType: Int, imgWidth: Float,
        imgHeight: Float, time: Int,isLongPressedFrameTask: Boolean = false
    ): Bitmap?

    /**
     * 向外界回调当前长按获取到的缩略帧
     *
     * @param bitmap 缩略帧图片
     * @param time bitmap的时间点
     * @param rulerTime 时间轴当前指向的时间点
     */
    fun onCurrentTimeThumbnailFrameResponse(bitmap: Bitmap, time: Int, rulerTime: Int)

}