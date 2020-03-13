package com.example.seungwon.openglestriangle.blur.rect

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.example.seungwon.openglestriangle.R

class BlurRectActivity : AppCompatActivity(), View.OnTouchListener {
    private var glSurfaceView: GLSurfaceView? = null
    private val renderer: BlurRectRenderer2 = BlurRectRenderer2(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur_rect)

        glSurfaceView = findViewById(R.id.gl_surface)
        glSurfaceView?.let {
            it.setEGLContextClientVersion(2)
            it.setRenderer(renderer)
            // Draw only `requestRender()` called.
            it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            it.setOnTouchListener(this)
        }
        val blurOffsetSeekBar = findViewById<SeekBar>(R.id.blur_offset_seekbar)
        val blurOffsetSeekBarTextView = findViewById<TextView>(R.id.blur_offset_seekbar_text)
        blurOffsetSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val offset = (progress / 100f)
                Log.d("BlurRectActivity", "onProgressChanged $offset")
                glSurfaceView?.queueEvent {
                    renderer.blurOffset = offset
                    glSurfaceView?.requestRender()
                }
                blurOffsetSeekBarTextView.text = "offset($offset): "
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        val blurIntensitySeekBar = findViewById<SeekBar>(R.id.blur_intensity_seekbar)
        val blurIntensitySeekBarTextView = findViewById<TextView>(R.id.blur_intensity_seekbar_text)
        blurIntensitySeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val intensity =
                    when {
                        progress < 30 -> {
                            1
                        }
                        progress > 80 -> {
                            3
                        }
                        else -> {
                            2
                        }
                    }
                glSurfaceView?.queueEvent {
                    renderer.loopCount = intensity
                    Log.d("BlurRectActivity", "onProgressChanged2 ${renderer.loopCount}")
                    glSurfaceView?.requestRender()
                }
                blurIntensitySeekBarTextView.text = "intensity($intensity): "
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        Log.d("BlurRectActivity", "onTouch ${event.action} ${event.x} ${event.y}")

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.rectStartPointX = event.x
                renderer.rectStartPointY = event.y
//                Log.d(TAG, "ontouch matrix ${event.x} ${event.y}")
//                val matrix2 = android.graphics.Matrix()
//                matrix2.setScale(1440 / 1000f, 1f)
//                event.transform(matrix2)
//                Log.d(TAG, "ontouch matrix2 ${event.x} ${event.y}")
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                glSurfaceView?.queueEvent {
                    renderer.onDrawBlurRect(
                        event.x,
                        event.y
                    ) { glSurfaceView?.requestRender() }
                }
                return true
            }
        }
        return false
    }

    companion object {
        private const val TAG = "BlurRectActivity"
    }
}
