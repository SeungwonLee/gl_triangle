package com.example.seungwon.openglestriangle.blur.rect

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.seungwon.openglestriangle.R

class BlurRectActivity : AppCompatActivity(), View.OnTouchListener {
    private var glSurfaceView: GLSurfaceView? = null
    private val renderer: BlurRectRenderer = BlurRectRenderer(this)

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
        val imageView = findViewById<ImageView>(R.id.blur_rect_img_view)
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.park_dotori))
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
}
