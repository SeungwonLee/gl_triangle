package com.example.seungwon.openglestriangle.gles30.triangle

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.seungwon.openglestriangle.gles30.drawer.Drawer
import com.example.seungwon.openglestriangle.gles30.drawer.SquareDrawerVao
import com.example.seungwon.openglestriangle.gles30.drawer.TriangleDrawerVao
import com.example.seungwon.openglestriangle.util.gles30.GlErrorUtil
import com.example.seungwon.openglestriangle.util.gles30.ProgramLoader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TriangleRendererWithVBO(val context: Context) : GLSurfaceView.Renderer {
    var isTriangle: Boolean = true
        set(value) {
            triangleDrawer30 = if (value) {
                TriangleDrawerVao()
            } else {
                SquareDrawerVao()
            }.also {
                it.release()
                it.init(programHandle)
            }
            field = value
        }

    private var programHandle: Int = 0

    private var triangleDrawer30: Drawer = TriangleDrawerVao()

    override fun onDrawFrame(gl: GL10?) {
        GlErrorUtil.checkGLError()
        triangleDrawer30.draw(programHandle)
        GlErrorUtil.checkGLError()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        programHandle = ProgramLoader.load(
            context,
            "shaders/gl_es_30/triangle.vert",
            "shaders/gl_es_30/triangle.frag"
        )
        GlErrorUtil.checkGLError()
        triangleDrawer30.init(programHandle)
        GlErrorUtil.checkGLError()
    }

    fun release() = triangleDrawer30.release()
}
