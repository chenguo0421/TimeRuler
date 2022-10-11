package com.siwencat.swui.timeruler.proxy

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
    fun onTimeChange(offset: Int)


    /**
     * 时间轴上的拖拽事件，将拖拽过程中时间轴指向的时间向外抛出
     *
     * @param offset 时间轴指向的时间偏移量 从0开始，最大为 24 * 60 * 60秒
     */
    fun onTimeRulerDrag(offset: Int)


    /**
     * 时间轴上的拖拽事件，将拖拽过程中时间轴指向的时间向外抛出
     *
     * @param offset 时间轴指向的时间偏移量 从0开始，最大为 24 * 60 * 60秒
     */
    fun onTimeRulerZooming(offset: Int)

    /**
     * 时间轴上的拖拽事件，将拖拽过程中时间轴指向的时间向外抛出
     *
     * @param offset 时间轴指向的时间偏移量 从0开始，最大为 24 * 60 * 60秒
     */
    fun onTimeRulerZoomStop(offset: Int)

}