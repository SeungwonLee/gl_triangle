package com.example.seungwon.openglestriangle.gles30.texture

import android.opengl.GLSurfaceView
import com.example.seungwon.openglestriangle.BaseActivity30

class TextureActivity : BaseActivity30() {
    override fun getRenderer(): GLSurfaceView.Renderer = TextureRenderer(this)
}
