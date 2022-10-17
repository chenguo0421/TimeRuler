package com.siwencat.swui.timeruler.proxy

import android.content.Context
import android.graphics.Color
import android.view.View
import com.siwencat.swui.timeruler.bean.ScaleType
import com.siwencat.swui.timeruler.utils.dp2px
import com.siwencat.swui.timeruler.view.SWUITimeRuler
import kotlin.collections.HashMap

/**
 * @Description TODO
 * @Creator ChenGuo
 * @Email wushengyuan1hao@163.com
 * @Date 09-29-2022 周四 16:23
 */
abstract class UITimeSectionView(context: Context) : View(context), TimeSectionProxyInterface {


    /**
     * Builder类，用于设置一些播放器的配置项，颜色，字体大小等
     *
     * @property context 上下文对象
     */
    class Builder(var context: Context) {
        var timelineTopMargin: Int = dp2px(context, 80f)//时间切片控件的时间线距离顶部的margin值
        var timelineHeight: Int = dp2px(context, 2f)//时间切片控件的时间线厚度（高度）
        var timelineColor: Int = Color.parseColor("#A8C5FF")//时间切片控件的时间线的颜色
        var timelineTextColor: Int = Color.parseColor("#FF000000")//时间切片控件的时间线上的时间文字的颜色
        var timelineTextBgColor: Int = Color.parseColor("#22BBBBBB")//时间切片控件的时间线上的时间文字的背景颜色
        var timeRulerTextColor: Int = Color.parseColor("#FF000000")//时间切片控件的时间轴上的时间文字的颜色
        var timeRulerGraduationColor: Int = Color.parseColor("#DCDCDC")//时间切片控件的时间轴上的刻度线颜色
        var timeRulerBgColor: Int = Color.parseColor("#F0F6FF")//时间切片控件的时间轴的背景色
        var timeRulerLineHeight: Int = dp2px(context, 1f)//时间切片控件的时间轴的刻度的厚度
        var timeClipBgColor: Int = Color.parseColor("#FFFFFFFF")//时间切片控件的时间切片滚动视图的背景色
        var timeClipLineColor: Int = Color.parseColor("#22BBBBBB")//时间切片控件的时间切片图片站连线颜色
        var timeMarqueeColor: Int = Color.parseColor("#FFFF0000")//时间切片控件的时间轴的选取框的颜色

        var leftRightViewProportion = 0.25f//左右两边的视图比例
        val timeLineBgHeight = dp2px(context, 30f)//时间切片控件的时间线上文字背景区域高度

        var thumbnailImageHeight = dp2px(context, 80f)//切片图片高度
        var thumbnailImageSpaceHeight = dp2px(context, 5f)//切片图片间距
        var thumbnailImageRadius = dp2px(context, 5f)//切片图片圆角
        var thumbnailFrameInterval = 50//拖拽时间点的缩略帧获取间隔，该值用于时间轴高度的除数，取值区间为1到时间轴高度的整数值

        var thumbnailCountMap = HashMap<ScaleType,Int>()


        /**
         * 设置时间切片控件的时间线距离顶部的margin值
         *
         * @param timelineTopMargin 距离顶部的margin值，单位为px
         * @return [Builder]
         */
        fun setTimelineTopMargin(timelineTopMargin: Int): Builder {
            this.timelineTopMargin = timelineTopMargin
            return this@Builder
        }

        /**
         * 设置时间切片控件的时间线的颜色
         *
         * @param timelineColor 时间线的颜色值
         * @return [Builder]
         */
        fun setTimelineColor(timelineColor: Int): Builder {
            this.timelineColor = timelineColor
            return this@Builder
        }

        /**
         * 设置时间切片控件的时间线上的时间文字的颜色
         *
         * @param timelineTextColor 时间线上的文字的颜色值
         * @return [Builder]
         */
        fun setTimelineTextColor(timelineTextColor: Int): Builder {
            this.timelineTextColor = timelineTextColor
            return this@Builder
        }

        /**
         * 设置时间切片控件的时间线上的时间文字的背景颜色
         *
         * @param timelineTextBgColor 时间线上的文字的背景颜色值
         * @return [Builder]
         */
        fun setTimelineTextBgColor(timelineTextBgColor: Int): Builder {
            this.timelineTextBgColor = timelineTextBgColor
            return this@Builder
        }

        /**
         * 设置时间切片控件的时间轴上的时间文字的颜色
         *
         * @param timeRulerTextColor 时间轴上的时间文字的颜色
         * @return [Builder]
         */
        fun setTimeRulerTextColor(timeRulerTextColor: Int): Builder {
            this.timeRulerTextColor = timeRulerTextColor
            return this@Builder
        }

        /**
         * 设置时间切片控件的时间轴上的刻度线颜色
         *
         * @param timeRulerGraduationColor 时间轴上的刻度线颜色
         * @return [Builder]
         */
        fun setTimeRulerGraduationColor(timeRulerGraduationColor: Int): Builder {
            this.timeRulerGraduationColor = timeRulerGraduationColor
            return this@Builder
        }

        /**
         * 设置时间切片控件的时间轴的背景色
         *
         * @param timeRulerBgColor 时间轴的背景色
         * @return [Builder]
         */
        fun setTimeRulerBgColor(timeRulerBgColor: Int): Builder {
            this.timeRulerBgColor = timeRulerBgColor
            return this@Builder
        }

        /**
         * 时间切片控件的时间切片滚动视图的背景色
         *
         * @param timeClipBgColor 时间切片滚动视图的背景色
         * @return [Builder]
         */
        fun setTimeClipBgColor(timeClipBgColor: Int): Builder {
            this.timeClipBgColor = timeClipBgColor
            return this@Builder
        }

        /**
         * 时间切片控件的时间切片图片站连线颜色
         *
         * @param timeClipLineColor 时间切片图片站连线颜色
         * @return [Builder]
         */
        fun setTimeClipLineColor(timeClipLineColor: Int): Builder {
            this.timeClipLineColor = timeClipLineColor
            return this@Builder
        }

        /**
         * 时间切片控件的时间轴的选取框的颜色
         *
         * @param timeMarqueeColor 时间轴的选取框的颜色
         * @return [Builder]
         */
        fun setTimeMarqueeColor(timeMarqueeColor: Int): Builder {
            this.timeMarqueeColor = timeMarqueeColor
            return this@Builder
        }

        /**
         * 时间轴控件左右两边的比例，取值在0-1之间
         *
         * @param proportion
         * @return
         */
        fun setLeftRightViewProportion(proportion: Float): Builder {
            this.leftRightViewProportion = proportion
            if (this.leftRightViewProportion < 0) {
                this.leftRightViewProportion = 0f
            }
            if (this.leftRightViewProportion > 1) {
                this.leftRightViewProportion = 1f
            }
            return this@Builder
        }

        /**
         * 设置时间基准线厚度（高度）
         *
         * @param height
         * @return
         */
        fun setTimelineHeight(height: Int): Builder {
            this.timelineHeight = height
            return this@Builder
        }

        /**
         * 设置不同的缩放倍率下的缩略图的张数
         * 不设置时，使用默认的
         * @param thumbnailCountMap 不同的缩放倍率数组
         */
        fun setRenderThumbnailCountByScaleType(
            thumbnailCountMap: HashMap<ScaleType,Int>
        ): Builder {
            this.thumbnailCountMap = thumbnailCountMap
            return this@Builder
        }


        /**
         * 设置切片图片高度，单位px
         *
         * @param thumbnailImageHeight 切片图片高度，单位px
         * @return
         */
        fun setThumbnailImageHeight(thumbnailImageHeight: Int): Builder {
            this.thumbnailImageHeight = thumbnailImageHeight
            return this@Builder
        }

        /**
         * 设置切片图片间距，单位px
         *
         * @param thumbnailImageSpaceHeight 切片图片间距，单位px
         * @return
         */
        fun setThumbnailImageSpaceHeight(thumbnailImageSpaceHeight: Int): Builder {
            this.thumbnailImageSpaceHeight = thumbnailImageSpaceHeight
            return this@Builder
        }

        /**
         * 设置时间轴切片图片的圆角，单位px
         *
         * @param radius 圆角大小，单位px
         * @return
         */
        fun setThumbnailImageRadius(radius: Int): Builder {
            this.thumbnailImageRadius = radius
            return this@Builder
        }

        /**
         * 拖拽时间点的缩略帧获取间隔，该值用于时间轴高度的除数，取值区间建议在 1 到 时间轴高度 之间的整数值
         *
         * @param interval 取帧间隔，该值相对于时间轴高度，若传入值小于1，则默认为1   例如：当 interval = 50时，则每拖拽 时间轴高度的1/50,取一次拖拽的缩略帧
         * @return
         */
        fun setThumbnailFrameInterval(interval: Int): Builder {
            if (interval < 1) {
                this.thumbnailFrameInterval = 1
            } else {
                this.thumbnailFrameInterval = interval
            }
            return this@Builder
        }

        fun build(): UITimeSectionView {
            initThumbnailCountMapIfNeed()
            val tsLayout = SWUITimeRuler(context)
            tsLayout.build(this@Builder)
            return tsLayout
        }

        private fun initThumbnailCountMapIfNeed() {
            if (thumbnailCountMap.size <= 0) {
                thumbnailCountMap[ScaleType.HOUR] = 14400
                thumbnailCountMap[ScaleType.HALF_HOUR] = 7200
                thumbnailCountMap[ScaleType.TEN_MINUTE] = 3600
                thumbnailCountMap[ScaleType.MINUTE] = 1200
                thumbnailCountMap[ScaleType.TEN_SECOND] = 60
                thumbnailCountMap[ScaleType.SECOND] = 10
            }
        }
    }

    abstract fun build(builder: Builder)

}