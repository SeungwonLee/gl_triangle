package com.example.seungwon.openglestriangle.blur

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.seungwon.openglestriangle.R

class BlurActivity : AppCompatActivity() {
    private var glSurfaceView: GLSurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)

        glSurfaceView = findViewById(R.id.blur_surface_view)
        glSurfaceView?.let {
            it.setRenderer(BlurRenderer(this))
            it.setEGLContextClientVersion(2)
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
