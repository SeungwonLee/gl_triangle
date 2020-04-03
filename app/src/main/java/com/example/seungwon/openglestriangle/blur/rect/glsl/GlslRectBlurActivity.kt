package com.example.seungwon.openglestriangle.blur.rect.glsl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.seungwon.openglestriangle.R

class GlslRectBlurActivity : AppCompatActivity(), View.OnTouchListener {

    private var glSurfaceView: GLSurfaceView? = null
    private var renderer: GlslBlurRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        renderer = GlslBlurRenderer(this)

        glSurfaceView = findViewById(R.id.gl_surface)
        glSurfaceView?.let {
            it.setEGLContextClientVersion(2)
            it.setRenderer(renderer)
            it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            it.setOnTouchListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

    var startRawX = 0F
    var startRawY = 0F

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer?.let {
                    it.textureRectStartPointX = event.x
                    it.textureRectStartPointY = event.y

                    startRawX = event.rawX
                    startRawY = event.rawY

                    Log.d(TAG, "event ACTION_DOWN ${event.x} ${event.y}")
                    Log.d(TAG, "event ACTION_DOWN R ${event.rawX} ${event.rawY}")
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "event ACTION_MOVE ${event.x} ${event.y}")
                Log.d(TAG, "event ACTION_MOVE R ${event.rawX} ${event.rawY}")
                return true
            }
            MotionEvent.ACTION_UP -> {
                glSurfaceView?.queueEvent {
                    renderer?.let {
                        it.textureRectEndPointX = it.textureRectStartPointX + (event.rawX - startRawX)
                        it.textureRectEndPointY = it.textureRectStartPointY + (event.rawY - startRawY)

                        Log.d(TAG, "event ACTION_UP ${event.x} ${event.y}")
                        Log.d(TAG, "event ACTION_UP R ${event.rawX} ${event.rawY}")
//                        it.textureRectEndPointX = event.x
//                        it.textureRectEndPointY = event.y
                        it.onDrawBlurRect { glSurfaceView?.requestRender() }
                    }
                }
                return true
            }
        }
        return false
    }

    companion object {
        private const val TAG = "BlurScaledActivity"
        var SCALED_WIDTH = 1440f
        var SCALED_HEIGHT = 2240f
    }
}
