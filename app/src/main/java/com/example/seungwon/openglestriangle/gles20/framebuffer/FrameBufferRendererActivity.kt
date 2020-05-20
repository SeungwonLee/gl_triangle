package com.example.seungwon.openglestriangle.gles20.framebuffer

import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.seungwon.openglestriangle.R

class FrameBufferRendererActivity : AppCompatActivity() {

    var surfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strip)

        val surfaceView = findViewById<GLSurfaceView>(R.id.surface_view)
        surfaceView.setEGLContextClientVersion(2)
        surfaceView.setRenderer(FrameBufferRenderer(this))
    }

    override fun onPause() {
        super.onPause()
        surfaceView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        surfaceView?.onResume()
    }
}
