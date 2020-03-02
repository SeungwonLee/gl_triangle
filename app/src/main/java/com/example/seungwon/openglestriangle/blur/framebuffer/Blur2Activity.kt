package com.example.seungwon.openglestriangle.blur.framebuffer

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.seungwon.openglestriangle.R

class Blur2Activity : AppCompatActivity() {
    private var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur2)

        glSurfaceView = findViewById(R.id.blur_framebuffer_gl_surface)
        glSurfaceView?.let {
            it.setEGLContextClientVersion(2)
            it.setRenderer(
                BlurFrameBufferRenderer(
                    this
                )
            )
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
}
