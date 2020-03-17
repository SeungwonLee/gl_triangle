package com.example.seungwon.openglestriangle.blur.stackblur

import android.opengl.GLSurfaceView
import com.example.seungwon.openglestriangle.BaseActivity

class StackBlurActivity : BaseActivity() {
    override fun getRenderer(): GLSurfaceView.Renderer = StackBlurRendererHoko(this)
}
