package com.example.seungwon.openglestriangle.text

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.seungwon.openglestriangle.R

class TextureActivity : AppCompatActivity() {

    private var surfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_texture)

        val surfaceView = findViewById<GLSurfaceView>(R.id.texture_surface_view)
        surfaceView.setEGLContextClientVersion(2)
        surfaceView.setRenderer(TextureRenderer(this))
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
