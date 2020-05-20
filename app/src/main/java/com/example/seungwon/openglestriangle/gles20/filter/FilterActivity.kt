package com.example.seungwon.openglestriangle.gles20.filter

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.gles20.projection.translation.FilterRenderer

class FilterActivity : AppCompatActivity() {

    var surfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strip)

        val surfaceView = findViewById<GLSurfaceView>(R.id.surface_view)
        surfaceView.setEGLContextClientVersion(2)
        surfaceView.setRenderer(FilterRenderer(this))
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
