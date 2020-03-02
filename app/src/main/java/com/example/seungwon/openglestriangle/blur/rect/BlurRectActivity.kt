package com.example.seungwon.openglestriangle.blur.rect

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.example.seungwon.openglestriangle.R

class BlurRectActivity : AppCompatActivity() {
    private var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur_rect)

        glSurfaceView = findViewById(R.id.gl_surface)
        glSurfaceView?.let {
            it.setEGLContextClientVersion(2)
//            it.setZOrderOnTop(true)
//            it.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
//            it.holder.setFormat(PixelFormat.RGBA_8888)
            it.setRenderer(BlurRectRenderer(this))
//            it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
        val imageView = findViewById<ImageView>(R.id.blur_rect_img_view)
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.park_dotori))
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }
}
