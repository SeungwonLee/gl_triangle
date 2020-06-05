package com.example.seungwon.openglestriangle

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup

abstract class BaseActivity : AppCompatActivity() {
    protected var glSurfaceView: GLSurfaceView? = null
    protected var parentView: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        parentView = findViewById(R.id.parent_layout)
        glSurfaceView = findViewById(R.id.gl_surface)
    }

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
