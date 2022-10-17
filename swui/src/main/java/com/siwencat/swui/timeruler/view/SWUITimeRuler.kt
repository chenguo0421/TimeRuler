package com.siwencat.swui.timeruler.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.MotionEvent
import com.siwencat.swui.R
import com.siwencat.swui.manager.cache.BitmapCacheBean
import com.siwencat.swui.timeruler.bean.FrameBitmap
import com.siwencat.swui.timeruler.bean.ScaleType
import com.siwencat.swui.timeruler.bean.VideoClip
import com.siwencat.swui.timeruler.proxy.TimeSectionListener
import com.siwencat.swui.timeruler.proxy.UITimeSectionView
import com.siwencat.swui.timeruler.utils.getStringFormat
import com.siwencat.swui.timeruler.utils.spacing
import com.siwencat.swui.timeruler.utils.timeTo24H
import java.util.*
import kotlin.math.abs


/**
 * @Description 纵向时间轴控件
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 09-16-2022 周五 16:23
 */
internal class SWUITimeRuler(context:Context) : UITimeSectionView(context){

    private lateinit var buildParams: Builder

    private val DAYS = 1 //时间轴天数，当前设计最多一天，即不跨天
    private val MAX = 86400.0f * DAYS //时间轴最小精度为秒，则最大时间范围是 24 * 60 * 60 = 86400秒

    private var viewWidth:Float = 0f //时间轴宽度，由外界设定
    private var viewHeight:Float = 0f//时间轴高度，由外界设定

    private var bgPaint = Paint()//视图背景画笔

    private var baseLinePaint = Paint()//时间基准线画笔
    private var baseLineTextRect = RectF()

    private var timeRulerPaint = Paint()//时间轴画笔

    private var clipLinePaint = Paint()//时间轴时间切片画笔，绘制时间切片的连接线
    private var clipImagePaint = Paint()//时间轴时间切片画笔，绘制切片图片
    //    private var srcClipRect = Rect()//时间轴时间切片图片位置矩阵
    private var desClipRect = RectF()//时间轴时间切片图片位置矩阵

    private var eventTypePaint = Paint()//时间类型填充颜色画笔

    private var scaleType = ScaleType.HOUR//当前时间轴的缩放类型

    private var baseLineTextRectWidth = 0f//时间基准线上的文字背景区域宽度
    private var selectTimeStr = "00:00:00"//时间基准线上的文字


    private val maxScale = 2160f//时间轴缩放最大比例，最小比例是1
    private var scale = 1f//时间轴缩放比例
    private var currentScale = 1f//临时的缩放倍数
    private var lastScale = 1f//时间轴上一次的缩放比例

    private var timeOffset:Float = 0f//时间偏移量，时间偏移指的是当天以秒为单位的时间偏移量，例如时间偏移为 1 * 60 * 60时，时间轴的时间线指向的是01:00:00刻度
    private var downTimeOffset: Float = 0f//记录每次单指按下时的时间偏移，如果松开单指时，该时间和timeOffset相等，则不回调onTimeRulerDragStop函数

    private var pointCount = 0//触摸事件中，手指触摸统计，单指，两指

    private var spaceSecond = 0f//每秒在时间轴上的间距

    private var touchStartX = 0f//触摸事件按下点的X坐标
    private var touchStartY = 0f//触摸事件按下点的Y坐标

    private var zoomStartDis = 0.0f//缩放开始，双指按下时的距离
    private var zoomEndDis = 0.0f//双指缩放进行时的双指之间的距离


    private val showDownloadView = false //显示下载界面

    private var listener: TimeSectionListener? = null

    private var videoClips = mutableListOf<VideoClip>()
    private var eventTypeRect = RectF()

    private var startDayTime: Int = 0
    private var devId: String = ""
    private var channelIndex: Int = 0
    private var streamType: Int = 0



    private var bitmap:Bitmap = (resources.getDrawable(R.mipmap.test_clip_dup) as BitmapDrawable).bitmap
    private var errBitmap:Bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.test_clip_dup)

    private var queryThumbnailFrameRunnable = Runnable {
        queryThumbnailFrame()
    }


    private var lastInvalidateTime: Long = 0

    init {


        //        val options = BitmapFactory.Options()
//        options.inJustDecodeBounds = true
//        BitmapFactory.decodeResource(resources, R.mipmap.test_clip_dup, options)
//
//        //set inSampleSize
//
//        //set inSampleSize
//        options.inSampleSize = 1
//
//        //set inJustDecodeBounds = false;
//
//        //set inJustDecodeBounds = false;
//        options.inJustDecodeBounds = false
//
//        bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.test_clip_dup)
    }





    override fun onDraw(canvas: Canvas) {
        drawBackgroundColor(canvas)//绘制背景色
        drawTimeTickMark(canvas)//绘制时间刻度线及文字
        drawVideoClipFill(canvas)//绘制录像片段填充区块
        if (showDownloadView) {
            drawDownloadMask(canvas)//绘制下载时间选框
        }
        drawBaseLine(canvas)//绘制时间基准线
    }

    /**
     * 绘制下载时间选框
     *
     * @param canvas
     */
    private fun drawDownloadMask(canvas: Canvas) {

    }

    /**
     * 绘制时间点的缩略帧
     *
     * @param canvas
     * @param yPx 需要绘制切片的时间轴时间点的y坐标
     * @param time 需要绘制切片的时间轴时间点的y坐标
     */
    private fun drawThumbnailFrame(canvas: Canvas, yPx: Float, time: Int) {
//        Log.d("drawThumbnailFrame1","desClipRect.top = ${desClipRect.top} , desClipRect.bottom = ${desClipRect.bottom} , yPx = $yPx , yPx - buildParams.thumbnailImageHeight / 2  = ${yPx - buildParams.thumbnailImageHeight / 2 }")

        val startX = viewWidth * buildParams.leftRightViewProportion
        val stopX = startX + (viewWidth - startX) / 2

        desClipRect.left = stopX
        desClipRect.right = viewWidth - viewWidth / 20
        desClipRect.top = yPx - buildParams.thumbnailImageHeight / 2
        desClipRect.bottom = yPx + buildParams.thumbnailImageHeight / 2


        val bitmap = listener?.onRequestThumbnailFrame(devId, channelIndex, streamType,desClipRect.right - desClipRect.left,desClipRect.bottom - desClipRect.top, time + startDayTime - (time % 10)) ?: errBitmap

        clipLinePaint.isAntiAlias = true
        clipLinePaint.style = Paint.Style.FILL
        clipLinePaint.strokeWidth = buildParams.timeRulerLineHeight.toFloat()
        clipLinePaint.color = buildParams.timeClipLineColor

        canvas.drawLine(
            startX,
            yPx,
            stopX,
            yPx,
            clipLinePaint
        )


        clipImagePaint.isAntiAlias = true
//        Log.d("drawThumbnailFrame3","desClipRect.top = ${desClipRect.top} , desClipRect.bottom = ${desClipRect.bottom} , yPx = $yPx")

//        canvas.drawBitmap(getRoundedCornerBitmap(bitmap ?: errBitmap),null,desClipRect,clipImagePaint) //以圆角的方式绘制
        canvas.drawBitmap(bitmap ?: errBitmap,null,desClipRect,clipImagePaint) //以非圆角的方式绘制
    }



    //获得圆角图片的方法
    private fun getRoundedCornerBitmap( bitmap:Bitmap):Bitmap{

        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888);
        val canvas = Canvas(output)

        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        clipImagePaint.xfermode = null
        clipImagePaint.isAntiAlias = true
        canvas.drawARGB(1,255,255,255)
//        clipImagePaint.color = color
        clipImagePaint.color = buildParams.timeClipBgColor
        canvas.drawRoundRect(rectF, buildParams.thumbnailImageRadius.toFloat(), buildParams.thumbnailImageRadius.toFloat(), clipImagePaint)
        //在源图像和目标图像相交的地方绘制【源图像】，在不相交的地方绘制【目标图像】，相交处的效果受到源图像和目标图像alpha的影响
        clipImagePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(bitmap, rect, rect, clipImagePaint)
        return output
    }



    /**
     * 绘制录像片段填充区块
     *
     * @param canvas
     */
    private fun drawVideoClipFill(canvas: Canvas) {
        if (videoClips.isEmpty()) {
            return
        }
        //计算从画布的0位置到时间基准线位置的秒数
        val timelineSecond = (buildParams.timelineTopMargin / viewHeight) * (MAX / scale)
        //计算一秒的间距
        spaceSecond = buildParams.timelineTopMargin / timelineSecond
//        获取当前时间区间，只绘制时间区间内的刻度,startTime为时间区间的开始时间
        val startTime = if (timelineSecond + timeOffset > MAX / scale) {
            (timelineSecond + timeOffset - (MAX / scale) + 0.5).toInt()
        } else {
            0
        }
//        获取当前时间区间，只绘制时间区间内的刻度，endTime为时间区间的结束时间
        var endTime = if (startTime == 0) {
            (timeOffset + timelineSecond + 0.5).toInt()
        } else {
            startTime + (MAX / scale + 0.5).toInt()
        }

        var startClipIndex = -1
        var endClipIndex = -1
        for (index in 0 until videoClips.size) {
            if (videoClips[index].endTime > MAX.toInt()) {//将end限制最大值为时间轴最大值MAX
                videoClips[index].endTime = MAX.toLong()
            }
//            Log.d("drawVideoClipFill1","startTime = $startTime ,endTime = $endTime, videoClips[index].startTime = ${videoClips[index].startTime} , videoClips[index].endTime = ${videoClips[index].endTime},  eventTypeRect.left = ${eventTypeRect.left} ， eventTypeRect.right = ${eventTypeRect.right} , eventTypeRect.bottom = ${eventTypeRect.bottom} ,   eventTypeRect.top = ${ eventTypeRect.top}")
            if (videoClips[index].endTime >= startTime && startClipIndex == -1) {
                startClipIndex = index
            }

            if (videoClips[index].startTime < endTime && startClipIndex != -1) {
                endClipIndex = index
            }
        }

//        Log.d("drawVideoClipFill2","startTime = $startTime ,endTime = $endTime, startClipIndex = $startClipIndex , endClipIndex = $endClipIndex")
        //可视时间区间内存在录像片段，则绘制填充颜色
        if (startClipIndex >= 0 && endClipIndex >= 0 && endClipIndex >= startClipIndex && endClipIndex < videoClips.size) {
            for (index in startClipIndex..endClipIndex) {

                eventTypeRect.left = 0f
                eventTypeRect.right = buildParams.leftRightViewProportion * viewWidth * 0.2f
                eventTypeRect.bottom = if (videoClips[index].startTime < startTime) {
                    viewHeight
                } else {
                    (endTime - videoClips[index].startTime) * spaceSecond
                }
                eventTypeRect.top = if (videoClips[index].endTime > endTime) {
                    0f
                } else {
                    (endTime - videoClips[index].endTime) * spaceSecond
                }
//                Log.d("drawVideoClipFill3","startTime = $startTime ,endTime = $endTime, videoClips[index].startTime = ${videoClips[index].startTime} , videoClips[index].endTime = ${videoClips[index].endTime},  eventTypeRect.left = ${eventTypeRect.left} ， eventTypeRect.right = ${eventTypeRect.right} , eventTypeRect.bottom = ${eventTypeRect.bottom} ,   eventTypeRect.top = ${ eventTypeRect.top}")
                eventTypePaint.isAntiAlias = true
                eventTypePaint.style = Paint.Style.FILL
                eventTypePaint.color = videoClips[index].clipColor
                canvas.drawRect(eventTypeRect,eventTypePaint)
            }
        }
    }



    /**
     * 绘制时间刻度线及文字
     *
     * @param canvas
     */
    private fun drawTimeTickMark(canvas: Canvas) {

        val timeOffset = timeOffset
        val scale = scale
        //计算从画布的0位置到时间基准线位置的秒数
        val timelineSecond = (buildParams.timelineTopMargin / viewHeight) * (MAX / scale)
        //计算一秒的间距
        spaceSecond = buildParams.timelineTopMargin / timelineSecond

        val secondCount = (timelineSecond + timeOffset).toInt()

        //获取当前时间区间，只绘制时间区间内的刻度,startTime为时间区间的开始时间
        var startTime = if (timelineSecond + timeOffset > MAX / scale) {
            (timelineSecond + timeOffset - (MAX / scale) + 0.5).toInt()
        } else {
            0
        }
//        获取当前时间区间，只绘制时间区间内的刻度，endTime为时间区间的结束时间
        var endTime = if (startTime == 0) {
            (timeOffset + timelineSecond + 0.5).toInt()
        } else {
            startTime + (MAX / scale + 0.5).toInt()
        }

        var timeRulerLineEnd = endTime
        if (timeRulerLineEnd > MAX) {
            timeRulerLineEnd = MAX.toInt()
        }
        //绘制时间轴垂直线
        timeRulerPaint.isAntiAlias = true
        timeRulerPaint.style = Paint.Style.FILL
        timeRulerPaint.color = buildParams.timeRulerGraduationColor
        timeRulerPaint.strokeWidth = buildParams.timeRulerLineHeight.toFloat()
        canvas.drawLine(
            viewWidth * buildParams.leftRightViewProportion - buildParams.timeRulerLineHeight.toFloat(),
            (endTime - startTime) * spaceSecond ,
            viewWidth * buildParams.leftRightViewProportion - buildParams.timeRulerLineHeight.toFloat(),
            (endTime - timeRulerLineEnd) * spaceSecond, timeRulerPaint
        )

        val lineHigh = viewWidth * buildParams.leftRightViewProportion * 3 / 10
        val lineShort = viewWidth * buildParams.leftRightViewProportion * 2 / 10

        var i = endTime
        if (i > MAX) {
            i = MAX.toInt()
        }
//        Log.d("drawTimeTickMark","startTime = $startTime ,endTime = $endTime")

        desClipRect.left = 0f
        desClipRect.right = 0f
        desClipRect.top = 0f
        desClipRect.bottom = 0f

        var spaceNum:Int = 1
        for (index in endTime downTo startTime) {
            if (i < 0) {
                return
            }
            val linePositionY = (endTime - i) * spaceSecond
            if (linePositionY < 0.0f || linePositionY > viewHeight) {
                i--
                continue
            }
//            Log.d("drawTimeTickMark","spaceSecond = $spaceSecond , buildParams.timelineTopMargin = ${buildParams.timelineTopMargin.toFloat()} , timeOffset = $timeOffset , secondCount = $secondCount ,timelineSecond = $timelineSecond , scale = $scale , viewHeight = $viewHeight  , i = $i ,linePositionY = $linePositionY")
            if (scale >= 1.0f && scale < 2.0f && i % 3600 == 0) {//用小时作为每个分隔的间隔 文字显示间隔是2小时
                scaleType = ScaleType.HOUR
                //获取小时刻度
                val tempStr = timeTo24H((i / 3600).toInt())
                //获取分钟
                val tempStr1 = 0
                //每隔2小时显示时间
                val text =
                    if (i % 7200 == 0) getStringFormat("%02d:%02d", tempStr, tempStr1) else ""
//                Log.d("drawTimeTickMark","stepNum = $stepNum , secondCount = $secondCount ,timelineSecond = $timelineSecond , scale = $scale , text = $text , viewHeight = $viewHeight  , i = $i ,linePositionY = $linePositionY")
                //显示时间时也同时显示较高的分割条
                val lineHeight: Float = if (i % 7200 == 0) lineHigh else lineShort
                drawCell(
                    canvas,
                    timeRulerPaint,
                    viewWidth * buildParams.leftRightViewProportion - lineHeight,
                    viewWidth * buildParams.leftRightViewProportion,
                    linePositionY,
                    text
                )

                if (i % ((buildParams.thumbnailCountMap[scaleType] ?: 14400)) == 0 && isClipsTime(i)) {//每4小时一个切片
                    val position = i / ((buildParams.thumbnailCountMap[scaleType] ?: 14400))

                    while ((buildParams.thumbnailCountMap[scaleType]
                            ?: 14400) * spaceNum * spaceSecond < (buildParams.thumbnailImageHeight + buildParams.thumbnailImageSpaceHeight)
                    ) {//存在图片重合
                        spaceNum *= 2
                    }

                    if (position % spaceNum == 0) {
//                        TLog.d("drawTimeTickMark","timeSpace = $timeSpace , position = $position , spaceNum = $spaceNum , secondCount = $secondCount ,timelineSecond = $timelineSecond , scale = $scale , text = $text , viewHeight = $viewHeight  , i = $i ,linePositionY = $linePositionY, buildParams.thumbnailImageHeight = ${buildParams.thumbnailImageHeight} , buildParams.thumbnailImageSpaceHeight = ${buildParams.thumbnailImageSpaceHeight}")
                        drawThumbnailFrame(canvas,linePositionY,i)
                    }
                }

                i -= 3600
            } else if (scale >= 2.0f && scale < 4.0f && i % 1800 == 0) { //用半小时作为每个分隔的间隔 文字显示间隔是1小时
                scaleType = ScaleType.HALF_HOUR
                //获取小时刻度
                val tempStr = timeTo24H(((i) / 3600).toInt())
                //获取分钟
                val tempStr1 = 0
                //每隔1小时显示时间
                val text =
                    if (i % 3600 == 0) getStringFormat("%02d:%02d", tempStr, tempStr1) else ""
//                Log.d("drawTimeTickMark","secondCount = $secondCount ,timelineSecond = $timelineSecond , scale = $scale , text = $text , viewHeight = $viewHeight , i = $i ,linePositionY = $linePositionY")
                //显示时间时也同时显示较高的分割条
                val lineHeight = if (i % 3600 == 0) lineHigh else lineShort

                drawCell(
                    canvas,
                    timeRulerPaint,
                    viewWidth * buildParams.leftRightViewProportion - lineHeight,
                    viewWidth * buildParams.leftRightViewProportion,
                    linePositionY,
                    text
                )

                if (i % ((buildParams.thumbnailCountMap[scaleType] ?: 7200)) == 0 && isClipsTime(i)) {//每两小时一个切片
                    val position = i / ((buildParams.thumbnailCountMap[scaleType] ?: 7200))
                    while ((buildParams.thumbnailCountMap[scaleType]
                            ?: 7200) * spaceNum * spaceSecond < (buildParams.thumbnailImageHeight + buildParams.thumbnailImageSpaceHeight)
                    ) {//存在图片重合
                        spaceNum *= 2
                    }

                    if (position % spaceNum == 0) {
                        drawThumbnailFrame(canvas,linePositionY,i)
                    }
                }
                i -= 1800
            } else if (scale >= 4.0f && scale < 24.0f && i % 600 == 0) {//用10分钟作为每个分隔的间隔 文字显示间隔是30分钟
                scaleType = ScaleType.TEN_MINUTE
                //获取小时
                val tempStr = timeTo24H(((i) / 3600).toInt())
                //获取分钟
                val tempStr1 = 30 * ((i) / 1800 % 2).toInt()

                //每隔30分钟显示时间
                val text =
                    if (i % 1800 == 0) getStringFormat("%02d:%02d", tempStr, abs(tempStr1)) else ""
//                Log.d("drawTimeTickMark","timelineSecond = $timelineSecond , scale = $scale , text = $text , viewHeight = $viewHeight , i = $i ,linePositionY = $linePositionY")
                //显示时间时也同时显示较高的分割条
                val lineHeight = if (i % 1800 == 0) lineHigh else lineShort

                drawCell(
                    canvas,
                    timeRulerPaint,
                    viewWidth * buildParams.leftRightViewProportion - lineHeight,
                    viewWidth * buildParams.leftRightViewProportion,
                    linePositionY,
                    text
                )

                if (i % ((buildParams.thumbnailCountMap[scaleType] ?: 3600)) == 0 && isClipsTime(i)) {//每一小时一个切片
                    val position = i / ((buildParams.thumbnailCountMap[scaleType] ?: 3600))
                    while ((buildParams.thumbnailCountMap[scaleType]
                            ?: 3600) * spaceNum * spaceSecond < (buildParams.thumbnailImageHeight + buildParams.thumbnailImageSpaceHeight)
                    ) {//存在图片重合
                        spaceNum *= 2
                    }

                    if (position % spaceNum == 0) {
                        drawThumbnailFrame(canvas,linePositionY,i)
                    }
                }
                i -= 600
            } else if (scale >= 24.0f && scale < 240.0f && i % 60 == 0) {//用1分钟作为每个分隔的间隔 文字显示间隔是10分钟
                scaleType = ScaleType.MINUTE
                //获取小时
                val tempStr = timeTo24H(((i) / 3600).toInt())
                //获取分钟
                val tempStr1 = 10 * ((i) / 600 % 6).toInt()

                //每隔10分钟显示时间
                val text =
                    if (i % 600 == 0) getStringFormat("%02d:%02d", tempStr, abs(tempStr1)) else ""

                //显示时间时也同时显示较高的分割条
                val lineHeight = if (i % 600 == 0) lineHigh else lineShort

                drawCell(
                    canvas,
                    timeRulerPaint,
                    viewWidth * buildParams.leftRightViewProportion - lineHeight,
                    viewWidth * buildParams.leftRightViewProportion,
                    linePositionY,
                    text
                )

                if (i % ((buildParams.thumbnailCountMap[scaleType] ?: 1200)) == 0 && isClipsTime(i)) {//每20分钟小时一个切片
                    val position = i / ((buildParams.thumbnailCountMap[scaleType] ?: 1200))
                    while ((buildParams.thumbnailCountMap[scaleType]
                            ?: 1200) * spaceNum * spaceSecond < (buildParams.thumbnailImageHeight + buildParams.thumbnailImageSpaceHeight)
                    ) {//存在图片重合
                        spaceNum *= 2
                    }

                    if (position % spaceNum == 0) {
                        drawThumbnailFrame(canvas,linePositionY,i)
                    }
                }
                i -= 60
            } else if (scale >= 240.0f && scale < 1440.0f && i % 10 == 0) {//用10秒钟作为每个分隔的间隔 文字显示间隔是1分钟
                scaleType = ScaleType.TEN_SECOND
                //获取小时
                val tempStr = timeTo24H(((i) / 3600).toInt())
                //获取分钟
                val tempStr1 = ((i) / 60 % 60).toInt()
                //每隔1分钟显示时间
                val text =
                    if (i % 60 == 0) getStringFormat("%02d:%02d", tempStr, abs(tempStr1)) else ""
//                Log.d("drawTimeTickMark","stepNum = $stepNum ,timelineSecond = $timelineSecond , scale = $scale , text = $text , viewHeight = $viewHeight , i = $i ,linePositionY = $linePositionY")
                //显示时间时也同时显示较高的分割条
                val lineHeight = if (i % 60 == 0) lineHigh else lineShort
                drawCell(
                    canvas,
                    timeRulerPaint,
                    viewWidth * buildParams.leftRightViewProportion - lineHeight,
                    viewWidth * buildParams.leftRightViewProportion,
                    linePositionY,
                    text
                )

                if (i % ((buildParams.thumbnailCountMap[scaleType] ?: 60)) == 0 && isClipsTime(i)) {//每1分钟一个切片
                    val position = i / ((buildParams.thumbnailCountMap[scaleType] ?: 60))
                    while ((buildParams.thumbnailCountMap[scaleType]
                            ?: 60) * spaceNum * spaceSecond < (buildParams.thumbnailImageHeight + buildParams.thumbnailImageSpaceHeight)
                    ) {//存在图片重合
                        spaceNum *= 2
                    }

                    if (position % spaceNum == 0) {
                        drawThumbnailFrame(canvas,linePositionY,i)
                    }
                }
                i -= 10
            } else if (scale >= 1440f) {
                scaleType = ScaleType.SECOND
                //获取小时
                val tempStr = timeTo24H(((i) / 3600).toInt())
                //获取分钟
                val tempStr1 = ((i) / 60 % 60).toInt()
                //获取秒钟
                val tempStr2 = ((i) % 60).toInt()

                //每隔10s显示时间
                val text =
                    if (i % 10 == 0) getStringFormat(
                        "%02d:%02d:%02d",
                        tempStr,
                        abs(tempStr1),
                        abs(tempStr2)
                    ) else ""
//                Log.d("drawTimeTickMark","timelineSecond = $timelineSecond , scale = $scale , text = $text , viewHeight = $viewHeight , i = $i ,linePositionY = $linePositionY")
                //显示时间时也同时显示较高的分割条
                val lineHeight = if (i % 10 == 0) lineHigh else lineShort
                drawCell(
                    canvas,
                    timeRulerPaint,
                    viewWidth * buildParams.leftRightViewProportion - lineHeight,
                    viewWidth * buildParams.leftRightViewProportion,
                    linePositionY,
                    text
                )

                if (i % ((buildParams.thumbnailCountMap[scaleType] ?: 10)) == 0 && isClipsTime(i)) {//每10秒钟一个切片
                    val position = i / ((buildParams.thumbnailCountMap[scaleType] ?: 10))
                    while ((buildParams.thumbnailCountMap[scaleType]
                            ?: 10) * spaceNum * spaceSecond < (buildParams.thumbnailImageHeight + buildParams.thumbnailImageSpaceHeight)
                    ) {//存在图片重合
                        spaceNum *= 2
                    }

                    if (position % spaceNum == 0) {
                        drawThumbnailFrame(canvas,linePositionY,i)
                    }
                }
                i--
            } else {
                i--
            }
        }
    }

    /**
     * 判断指定时间是否在某个录像事件内
     *
     * @param time 指定时间
     * @return true == 该时间属于某个录像事件时间段内
     */
    private fun isClipsTime(time: Int): Boolean {
        for (index in 0 until videoClips.size) {
            if (videoClips[index].startTime <= time && videoClips[index].endTime >= time) {
                return true
            }
        }
        return false
    }

    /**
     * @param canvas      画布
     * @param paint       画笔
     * @param startX X轴起始点
     * @param endX X轴终点
     * @param startY Y轴起点
     * @param paramString 时间字符串
     */
    private fun drawCell(
        canvas: Canvas,
        paint: Paint,
        startX: Float,
        endX: Float,
        startY: Float,
        paramString: String
    ) {
//        Log.d("drawLine1","startX = $startX , startY = $startY , endX = $endX , paramString = $paramString , viewHeight = $viewHeight , scale = $scale , startY = $startY")
        paint.strokeWidth = 2f
        canvas.drawLine(startX, startY, endX, startY, paint)

        paint.textSize = viewWidth * buildParams.leftRightViewProportion / 8
        paint.textAlign = Paint.Align.RIGHT
        paint.measureText(paramString)
        val localFontMetrics = paint.fontMetrics
//        Log.d("drawLine","startX = $startX , startY = $startY , endX = $endX , paramString = $paramString , viewHeight = $viewHeight , scale = $scale , startY = $startY")
        canvas.drawText(
            paramString,
            startX,
            startY + (localFontMetrics.bottom - localFontMetrics.top) / 4,
            paint
        )
    }


    /**
     * 绘制时间基准线
     *
     * @param canvas
     */
    private fun drawBaseLine(canvas: Canvas) {
        if (baseLineTextRectWidth == 0f) {
            baseLineTextRectWidth = buildParams.timeLineBgHeight * 3.2f
        }

        //绘制时间基准线
        baseLinePaint.isAntiAlias = true
        baseLinePaint.style = Paint.Style.FILL
        baseLinePaint.color = buildParams.timelineColor
        baseLinePaint.strokeWidth = buildParams.timelineHeight.toFloat()
        canvas.drawLine(
            0.0f, buildParams.timelineTopMargin.toFloat(), viewWidth * buildParams.leftRightViewProportion * 1.2f,
            buildParams.timelineTopMargin.toFloat(), baseLinePaint
        )
        canvas.drawLine(
            viewWidth * buildParams.leftRightViewProportion * 1.2f + baseLineTextRectWidth, buildParams.timelineTopMargin.toFloat(), viewWidth,
            buildParams.timelineTopMargin.toFloat(), baseLinePaint
        )

        //绘制时间基准线上的时间的背景色
        baseLinePaint.color = buildParams.timelineTextBgColor
        baseLineTextRect.left = viewWidth * buildParams.leftRightViewProportion * 1.2f
        baseLineTextRect.top = buildParams.timelineTopMargin + buildParams.timelineHeight / 2 - buildParams.timeLineBgHeight / 2f
        baseLineTextRect.right = viewWidth * buildParams.leftRightViewProportion * 1.2f + baseLineTextRectWidth
        baseLineTextRect.bottom = buildParams.timelineTopMargin + buildParams.timelineHeight / 2 + buildParams.timeLineBgHeight / 2f
        canvas.drawRoundRect(
            baseLineTextRect, 50f, 50f, baseLinePaint
        )


        //获取小时
        val tempStr = timeTo24H((timeOffset/ 3600).toInt())
        //获取分钟
        val tempStr1 = ((timeOffset) / 60 % 60).toInt()
        //获取秒钟
        val tempStr2 = ((timeOffset) % 60).toInt()

        selectTimeStr = getStringFormat(
            "%02d:%02d:%02d",
            tempStr,
            abs(tempStr1),
            abs(tempStr2)
        )
        //绘制时间基准线上的时间文字
        baseLinePaint.textAlign = Paint.Align.CENTER
        baseLinePaint.textSize = buildParams.timeLineBgHeight / 2f
        baseLinePaint.color = buildParams.timelineTextColor
        canvas.drawText(
            selectTimeStr,
            (baseLineTextRect.left + baseLineTextRect.right) / 2,
            (baseLineTextRect.top + baseLineTextRect.bottom) / 2 + (baseLinePaint.fontMetrics.bottom - baseLinePaint.fontMetrics.top) / 4,
            baseLinePaint
        )
    }


    /**
     * 绘制背景色
     *
     * @param canvas
     */
    private fun drawBackgroundColor(canvas: Canvas) {
        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL
        bgPaint.color = buildParams.timeRulerBgColor
        canvas.drawRect(
            0.0f, 0.0f, viewWidth * buildParams.leftRightViewProportion,
            viewHeight, bgPaint
        )

        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL
        bgPaint.color = buildParams.timeClipBgColor
        canvas.drawRect(
            viewWidth * buildParams.leftRightViewProportion, 0.0f, viewWidth,
            viewHeight, bgPaint
        )
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val f1 = event.x
        val bool = f1 < 0.0f
        if (bool && 0xFF and event.action != MotionEvent.ACTION_UP && 0xFF and event.action != MotionEvent.ACTION_CANCEL && 0xFF and event.action != MotionEvent.ACTION_POINTER_UP) {
            return true
        }


        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                pointCount = event.pointerCount
                touchStartX = event.x
                touchStartY = event.y
                downTimeOffset = timeOffset
                queryThumbnailFrame()
                listener?.onTimeRulerClickDown(timeOffset.toInt() + startDayTime)
//                Log.d("ACTION_POINTER_DOWN","onTouchEvent scale = $scale , lastScale = $lastScale,currentScale=$currentScale, pointCount =$pointCount")
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                isPressed = true
                pointCount = event.pointerCount
                if (pointCount == 2){
                    zoomStartDis = spacing(event)
                    lastScale = scale
                }
//                Log.d("ACTION_POINTER_DOWN","onTouchEvent scale = $scale , lastScale = $lastScale,currentScale=$currentScale, pointCount =$pointCount")
            }

            MotionEvent.ACTION_MOVE -> {
                if (pointCount == 2){
                    zoomEndDis = spacing(event)


                    val f6 = zoomEndDis / zoomStartDis
//                    Log.d("ACTION_MOVE","onTouchEvent scale = $scale ,f6 = $f6 , lastScale = $lastScale,currentScale=$currentScale")
                    currentScale = f6
                    scale = currentScale * lastScale


//                    Log.d("ACTION_MOVE1","onTouchEvent scale = $scale ,f6 = $f6 , lastScale = $lastScale,currentScale=$currentScale")
                    if (scale <= 1.0f) {
                        scale = 1.0f
                        currentScale = 1f

                        // return true;
                    }
                    if (scale >= maxScale) {
                        scale = maxScale
                        currentScale = maxScale
                        return true
                    }
                    invalidate()
                }else if (pointCount == 1) {
                    moveTimeRuler(event.x,event.y - touchStartY)
                    touchStartY = event.y
                    removeCallbacks(queryThumbnailFrameRunnable)
                    postDelayed(queryThumbnailFrameRunnable, 500)
                    listener?.onTimeRulerDrag(timeOffset.toInt() + startDayTime)

                    if (!isClipsTime(timeOffset.toInt())) {
                        listener?.onTimeRulerDragOutOfClips(timeOffset.toInt() + startDayTime)
                    }
                }



            }

            MotionEvent.ACTION_UP-> {
//                Log.d("ACTION_POINTER_UP","onTouchEvent scale = $scale , lastScale = $lastScale,currentScale=$currentScale")
                if(timeOffset.toInt() != downTimeOffset.toInt()){
                    listener?.onTimeRulerDragStop(timeOffset.toInt() + startDayTime)
                }
                removeCallbacks(queryThumbnailFrameRunnable)
                listener?.onTimeRulerClickUp(timeOffset.toInt() + startDayTime)
                lastScale = scale
                pointCount = 0
            }

            MotionEvent.ACTION_CANCEL -> {
                if(timeOffset.toInt() != downTimeOffset.toInt()){
                    listener?.onTimeRulerDragStop(timeOffset.toInt() + startDayTime)
                }
                removeCallbacks(queryThumbnailFrameRunnable)
                listener?.onTimeRulerClickUp(timeOffset.toInt() + startDayTime)
//                Log.d("ACTION_POINTER_UP","onTouchEvent scale = $scale , lastScale = $lastScale,currentScale=$currentScale")
                lastScale = scale
                pointCount = 0
            }

            MotionEvent.ACTION_POINTER_UP -> {
//                Log.d("ACTION_POINTER_UP","onTouchEvent scale = $scale , lastScale = $lastScale,currentScale=$currentScale")
                lastScale = scale
                pointCount = 0
            }
        }

        return true
    }

    var countPx = 0f
    var lastPXCount = 0
    private fun moveTimeRuler(f4: Float, f5: Float) {
        countPx += f5
        val pxCount = (countPx / (viewHeight / buildParams.thumbnailFrameInterval)).toInt()
        val offset = f5 / viewHeight * MAX / scale
//        Log.d("moveTimeRuler","onTouchEvent scale = $scale , lastScale = $lastScale,currentScale=$currentScale ， f5 = $f5 ,offset = $offset ,timeOffset = $timeOffset ")

        timeOffset += offset

        if (timeOffset <= 0) {
            timeOffset = 0f
        }
        if (timeOffset >= MAX) {
            timeOffset = MAX
        }
        listener?.onTimeChange(timeOffset.toInt())
        invalidate()


        if (abs(pxCount - lastPXCount) >= 1) {
            queryThumbnailFrame()
            lastPXCount = pxCount
        }

    }

    private fun queryThumbnailFrame() {
        val left =
            viewWidth * buildParams.leftRightViewProportion + (viewWidth - viewWidth * buildParams.leftRightViewProportion) / 2
        val localOffset = timeOffset.toInt()
        val time = startDayTime + localOffset - localOffset % 10
//        TLog.d(
//            "moveTimeRuler",
//            "lastPXCount = $lastPXCount, countPx = $countPx , onTouchEvent scale = $scale , lastScale = $lastScale,currentScale=$currentScale  ,timeOffset = $timeOffset ,time = $time , startDayTime = $startDayTime"
//        )

        val ret = listener?.onRequestThumbnailFrame(
            devId,
            channelIndex,
            streamType,
            viewWidth - viewWidth / 20 - left,
            buildParams.thumbnailImageHeight.toFloat(),
            time,
            true
        )
        if (ret != null) {
            val rulerTime =  (timeOffset + startDayTime).toInt()
            listener?.onCurrentTimeThumbnailFrameResponse(
                ret,
                time,
                rulerTime
            )
        }
    }


    override fun build(builder: Builder) {
        this.buildParams = builder
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            viewWidth = measuredWidth.toFloat()
            viewHeight = measuredHeight.toFloat()
        }
    }

    override fun setVideoClip(videoClips: List<VideoClip>) {
        this.videoClips.clear()
        this.videoClips.addAll(videoClips)
        for(item in videoClips){
            Log.d("setVideoClip","videoClip item = ${item.toString()}")
        }
        post { invalidate() }
    }

    override fun setThumbnails(bitmapCacheBean: BitmapCacheBean) {
        //长按事件获取到的当前时间轴指针指向的时间点的缩略帧（当前时间点最近的一个缩略帧，并不一定刚好在这个时间点）
        if (bitmapCacheBean.isLongPressedFrame) {
            val rulerTime = (timeOffset + startDayTime).toInt()
            listener?.onCurrentTimeThumbnailFrameResponse(
                bitmapCacheBean.bitmap,
                bitmapCacheBean.time,
                rulerTime
            )
        }

        //至少间隔1秒再刷新
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInvalidateTime > 1000){
            postInvalidate()
        }
        lastInvalidateTime = currentTime
    }

    /**
     * 设置时间偏移，单位为秒， 当两次设置时间相同时，时间轴不渲染，不响应
     * 时间偏移指的是当天以秒为单位的时间偏移量，例如时间偏移为 1 * 60 * 60时，时间轴的时间线指向的是01:00:00刻度
     * @param timeOffset 时间偏移，从0开始，最大为 24 * 60 * 60秒
     */
    override fun setTimeOffset(timeOffset: Int) {
        if (pointCount == 0) {//未按住的时候，才接受设置的值
            if (abs(timeOffset - this.timeOffset) >= 1) {
                this.timeOffset = timeOffset.toFloat()
                post { invalidate() }
            }
        }
    }

    override fun getTimeOffset(): Int {
        return timeOffset.toInt()
    }

    /**
     * 设置当天的0时0分0秒的时间戳，单位为秒
     * 当[dayStartTime]发生变化时，需要清空时间轴的录像片段集合，否则切换日期后，若切换的日期没有录像片段，则时间轴还是展示的上一次的录像片段集合
     *
     * @param dayStartTime 当天的0时0分0秒的时间戳，单位为秒
     */
    override fun setCurrentDayStartTime(dayStartTime: Int) {
        if (dayStartTime != this.startDayTime) {
            this.videoClips.clear()
            this.startDayTime = dayStartTime
        }
    }

    override fun setDeviceInfo(devId: String, channelIndex: Int, streamType: Int) {
        this.devId = devId
        this.channelIndex = channelIndex
        this.streamType = streamType
    }

    override fun setTimeSectionListener(listener: TimeSectionListener) {
        this.listener = listener
    }

}