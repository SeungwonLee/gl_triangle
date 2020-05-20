package com.example.seungwon.openglestriangle.gles20.mosaic

import android.opengl.GLSurfaceView
import com.example.seungwon.openglestriangle.BaseActivity

class MosaicActivity : BaseActivity() {
    override fun getRenderer(): GLSurfaceView.Renderer = MosaicRenderer(this)
}
