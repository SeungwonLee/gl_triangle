package com.example.seungwon.openglestriangle.square

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.seungwon.openglestriangle.R

class SquareActivity : AppCompatActivity() {

    private var surfaceView : GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_square)

        val surfaceView = findViewById<GLSurfaceView>(R.id.square_surface_view)
        surfaceView.setEGLContextClientVersion(2)
        surfaceView.setRenderer(SquareRenderer(this))
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
