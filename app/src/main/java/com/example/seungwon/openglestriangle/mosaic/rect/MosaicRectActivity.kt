package com.example.seungwon.openglestriangle.mosaic.rect

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.seungwon.openglestriangle.BaseActivity

class MosaicRectActivity : BaseActivity(), View.OnTouchListener {

    private val renderer = MosaicRectRenderer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView?.let {
            // Draw only `requestRender()` called.
            it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            it.setOnTouchListener(this)
        }
    }

    override fun getRenderer(): GLSurfaceView.Renderer = MosaicRectRenderer(this)

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        Log.d("MosaicRectActivity", "onTouch ${event.action} ${event.x} ${event.y}")

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
