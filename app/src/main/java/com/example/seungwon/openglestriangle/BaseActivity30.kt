package com.example.seungwon.openglestriangle

import android.opengl.GLSurfaceView
import android.os.Bundle

abstract class BaseActivity30 : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView?.let {
            it.setEGLContextClientVersion(3)
            it.setRenderer(getRenderer())
        }
    }

    abstract override fun getRenderer(): GLSurfaceView.Renderer
}
