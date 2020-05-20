package com.example.seungwon.openglestriangle.gles30.triangle

import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.seungwon.openglestriangle.BaseActivity
import com.example.seungwon.openglestriangle.R

class TriangleActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_triangle)
    }

    override fun getRenderer(): GLSurfaceView.Renderer = TriangleRenderer()
}
