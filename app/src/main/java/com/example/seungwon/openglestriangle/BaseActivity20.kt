package com.example.seungwon.openglestriangle

import android.opengl.GLSurfaceView
import android.os.Bundle

abstract class BaseActivity20 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView?.let {
            it.setEGLContextClientVersion(2)
            it.setRenderer(getRenderer())
            if (isDirty()) {
                // Draw only `requestRender()` called.
                it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            }
        }
    }

    abstract override fun getRenderer(): GLSurfaceView.Renderer

    private fun isDirty(): Boolean = false
}
