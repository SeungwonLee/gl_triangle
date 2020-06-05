package com.example.seungwon.openglestriangle.gles30.triangle

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.seungwon.openglestriangle.BaseActivity30
import com.example.seungwon.openglestriangle.R

class TriangleVaoVboActivity : BaseActivity30() {

    private val renderer: TriangleRendererWithVBO = TriangleRendererWithVBO(this)

    private var currentText: String = TRIANGLE_BUTTON

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        View.inflate(this, R.layout.activity_triangle_vao_vbo, parentView)

        glSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        findViewById<Button>(R.id.switcher_button).run {
            setOnClickListener {
                currentText = if (currentText == TRIANGLE_BUTTON) SQUARE_BUTTON else TRIANGLE_BUTTON
                text = if (currentText == TRIANGLE_BUTTON) TRIANGLE_BUTTON else SQUARE_BUTTON
                glSurfaceView?.queueEvent {
                    renderer.isTriangle = currentText == TRIANGLE_BUTTON
                    glSurfaceView?.requestRender()
                }
            }
        }
    }

    override fun getRenderer(): GLSurfaceView.Renderer = renderer

    override fun onDestroy() {
        super.onDestroy()
        renderer.release()
    }

    companion object {
        private const val SQUARE_BUTTON = "Square"
        private const val TRIANGLE_BUTTON = "Triangle"
    }
}
