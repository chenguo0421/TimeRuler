package com.siwencat.timeruler

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.siwencat.swui.timeruler.bean.VideoClip
import com.siwencat.swui.timeruler.proxy.TimeSectionListener
import com.siwencat.swui.timeruler.proxy.UITimeSectionView
import java.util.concurrent.locks.ReentrantLock

class MainActivity : AppCompatActivity(), TimeSectionListener {
    lateinit var view: UITimeSectionView
    var i: Int = 0
    var lock:ReentrantLock  = ReentrantLock()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = UITimeSectionView.Builder(this)
            .build()

//        view.post {
//            Thread{
//                while (true) {
//                    lock.lock()
//                    i++
//                    lock.unlock()
//                    view.setTimeOffset(i)
//                    Log.d("MainActivity","tempI = $i")
//                    Thread.sleep(1000)
//                }
//            }.start()
//        }

        view.setTimeSectionListener(this)

        findViewById<ConstraintLayout>(R.id.cl_content).addView(view,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        view.setVideoClip(getVideoClips())
    }


    val REC_TYPE_SCHEDULE = 0x02 // 排程录像
    val REC_TYPE_POS = 0x2000 //POS
    val REC_TYPE_MOTION = 0x04 // 移动侦测录像


    val REC_TYPE_CPC = 0x10000 //人流量统计
    val REC_TYPE_CDD = 0x20000 //人群密度检测
    val REC_TYPE_IPD = 0x40000 //人员入侵
    val REC_TYPE_OSC = 0x100 //物品看护侦测录像
    val REC_TYPE_AVD = 0x200 //异常侦测
    val REC_TYPE_TRIPWIRE = 0x400 //越界侦测
    val REC_TYPE_PERIMETER = 0x800 //区域入侵侦测
    val REC_TYPE_VFD = 0x1000 //人脸识别
    val REC_TYPE_SMART_MOTION = 0x4000 //智能移动
    val REC_TYPE_FACE_MATCH = 0x8000 //人脸比对
    val REC_TYPE_SMART_AOI_ENTRY = 0x80000 //区域进入
    val REC_TYPE_SMART_AOI_LEAVE = 0x100000 //区域离开
    val REC_TYPE_SMART_PLATE_MATCH = 0x400000 //车牌比对

    val REC_TYPE_INTELLIGENT: Int =
                REC_TYPE_CPC or
                REC_TYPE_CDD or
                REC_TYPE_IPD or
                        REC_TYPE_OSC or
                        REC_TYPE_AVD or
                        REC_TYPE_TRIPWIRE or
                        REC_TYPE_PERIMETER or
                        REC_TYPE_VFD or
                        REC_TYPE_SMART_MOTION or
                        REC_TYPE_FACE_MATCH or
                        REC_TYPE_SMART_AOI_ENTRY or
                        REC_TYPE_SMART_AOI_LEAVE or REC_TYPE_SMART_PLATE_MATCH


    val REC_TYPE_SMART_PASS_LINE = 0x200000 //过线统计
    val REC_TYPE_SMART_FIRE = 0x800000 //火点检测
    val REC_TYPE_SMART_TEMPERATURE = 0x1000000 //温度检测


    val REC_TYPE_OTHERS: Int =
        REC_TYPE_OSC or REC_TYPE_SMART_PASS_LINE or
                REC_TYPE_CDD or REC_TYPE_CPC or REC_TYPE_IPD or REC_TYPE_AVD or REC_TYPE_SMART_MOTION or REC_TYPE_SMART_FIRE or REC_TYPE_SMART_TEMPERATURE // AI 其他


    val REC_TYPE_PEA = REC_TYPE_PERIMETER or REC_TYPE_SMART_AOI_ENTRY or REC_TYPE_SMART_AOI_LEAVE// AI 区域

    val REC_TYPE_FACE = REC_TYPE_VFD or REC_TYPE_FACE_MATCH // AI 人脸

    val REC_TYPE_SENSOR = 0x08; // 传感器录像
    val REC_TYPE_MANUAL = 0x01; // 手动录像
    val REC_TYPE_PIR = 0x2000000; //红外

    private fun getVideoClips(): List<VideoClip> {
        val list = mutableListOf<VideoClip>()
        var i: Int = 0
        var stepCount = 4567
        for(index in 150 until 86400 step stepCount){
            if (index < 86400) {
                when (i % 13) {
                    0 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_SCHEDULE,
                                Color.parseColor("#0066FF")
                            )
                        )
                    }
                    1 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_POS,
                                Color.parseColor("#8300FF")
                            )
                        )
                    }
                    2 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_MOTION,
                                Color.parseColor("#FF9326")
                            )
                        )
                    }
                    3 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_INTELLIGENT,
                                Color.parseColor("#66FFFF")
                            )
                        )
                    }
                    4 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_OTHERS,
                                Color.parseColor("#66FFFF")
                            )
                        )
                    }
                    5 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_TRIPWIRE,
                                Color.parseColor("#FF00FF")
                            )
                        )
                    }
                    6 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_PEA,
                                Color.parseColor("#FFFF00")
                            )
                        )
                    }
                    7 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_SMART_PLATE_MATCH,
                                Color.parseColor("#008000")
                            )
                        )
                    }
                    8 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_FACE,
                                Color.parseColor("#00CCFF")
                            )
                        )
                    }
                    9 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_SENSOR,
                                Color.parseColor("#FF0000")
                            )
                        )
                    }
                    10 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_MANUAL,
                                Color.parseColor("#00FF00")
                            )
                        )
                    }
                    11 -> {
                        list.add(
                            VideoClip(
                                index.toLong(),
                                index.toLong() + stepCount,
                                REC_TYPE_PIR,
                                Color.parseColor("#FF6600")
                            )
                        )
                    }
                    12 -> {

                    }
                }
            }
            i++
        }

        return list
    }


    override fun onTimeChange(offset: Int) {
        lock.lock()
        i = offset
        lock.unlock()
    }

    override fun onTimeRulerDrag(offset: Int) {
    }

    override fun onTimeRulerZooming(offset: Int) {
    }

    override fun onTimeRulerZoomStop(offset: Int) {
    }

}