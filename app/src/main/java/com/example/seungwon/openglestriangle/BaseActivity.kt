package com.example.seungwon.openglestriangle

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        glSurfaceView = findViewById(R.id.gl_surface)
        glSurfaceView?.let {
            it.setEGLContextClientVersion(2)
            it.setRenderer(getRenderer())
            if (isDirty()) {
                // Draw only `requestRender()` called.
                it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            }
        }
    }

    open fun isDirty(): Boolean = false
    abstract fun getRenderer(): GLSurfaceView.Renderer

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }
}
