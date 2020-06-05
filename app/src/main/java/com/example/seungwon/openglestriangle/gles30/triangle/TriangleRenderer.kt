package com.example.seungwon.openglestriangle.gles30.triangle

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.seungwon.openglestriangle.gles30.drawer.TriangleDrawer
import com.example.seungwon.openglestriangle.util.gles30.ProgramLoader
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TriangleRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private var programHandle: Int = 0
    private var triangleDrawer: TriangleDrawer = TriangleDrawer()

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        triangleDrawer.draw()

        val inputBuffer = ByteBuffer.allocateDirect(4).asIntBuffer()
        Log.i("GL", "GL_ACTIVE_ATTRIBUTES = ${inputBuffer[0]}")
        GLES30.glGetProgramiv(programHandle, GLES30.GL_ACTIVE_ATTRIBUTES, inputBuffer)

        Log.i("GL", "GL_ACTIVE_ATTRIBUTES = ${inputBuffer[0]}")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        if (gl == null) {
            return
        }

        Log.d("GL", "GL = " + gl.javaClass)
        Log.d("GL", "GL_RENDERER = " + gl.glGetString(GL10.GL_RENDERER))
        Log.d("GL", "GL_VENDOR = " + gl.glGetString(GL10.GL_VENDOR))
        Log.d("GL", "GL_VERSION = " + gl.glGetString(GL10.GL_VERSION))
        Log.i("GL", "GL_EXTENSIONS = " + gl.glGetString(GL10.GL_EXTENSIONS))

        val inputArray = intArrayOf(1)
        gl.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, inputArray, 0)
        Log.i("GL", "GL_MAX_VERTEX_ATTRIBS = " + inputArray[0])

        gl.glGetIntegerv(GLES20.GL_MAX_VERTEX_UNIFORM_VECTORS, inputArray, 0)
        Log.i("GL", "GL_MAX_VERTEX_UNIFORM_VECTORS = " + inputArray[0])

        val values = IntArray(1)
        GLES30.glGetIntegerv(GLES30.GL_MAJOR_VERSION, values, 0)
        val majorVersion = values[0]
        GLES30.glGetIntegerv(GLES30.GL_MINOR_VERSION, values, 0)
        val minorVersion = values[0]
        if (GLES30.glGetError() == GLES30.GL_NO_ERROR) {
            Log.i(TAG, "iversion: $majorVersion.$minorVersion")
        }

        // Link verticesShader, fragmentShader to OpenGL
        programHandle = ProgramLoader.load(
            context,
            "shaders/gl_es_30/triangle.vert",
            "shaders/gl_es_30/triangle.frag"
        )

        triangleDrawer.init(programHandle)
    }

    companion object {
        private const val TAG = "TriangleRenderer"
    }
}

