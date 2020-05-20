package com.example.seungwon.openglestriangle.gles20.blur.stackblur

import android.opengl.GLSurfaceView
import com.example.seungwon.openglestriangle.BaseActivity

class StackBlurActivity : BaseActivity() {
    override fun getRenderer(): GLSurfaceView.Renderer = StackBlurRendererHoko(this)
}
