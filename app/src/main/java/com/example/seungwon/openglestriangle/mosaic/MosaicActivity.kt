package com.example.seungwon.openglestriangle.mosaic

import android.opengl.GLSurfaceView
import com.example.seungwon.openglestriangle.BaseActivity

class MosaicActivity : BaseActivity() {
    override fun getRenderer(): GLSurfaceView.Renderer = MosaicRenderer(this)
}
