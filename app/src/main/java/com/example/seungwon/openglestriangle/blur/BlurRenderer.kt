package com.example.seungwon.openglestriangle.blur

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.seungwon.openglestriangle.ProgramInfo
import com.example.seungwon.openglestriangle.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BlurRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val squareCoords: FloatArray = floatArrayOf(
        // X, Y
        1f, 1f, -1f, 1f, -1f, -1f,
        -1f, -1f, 1f, -1f, 1f, 1f
    )
    private val vertexBuffer: FloatBuffer

    private var passThroughProgram: Int = 0
    private var guassianVerticalProgram: Int = 0
    private var guassianHorizontalProgram: Int = 0

    init {
        vertexBuffer =
            ByteBuffer.allocateDirect(squareCoords.size * FLOAT_BYTE_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(squareCoords)
        vertexBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        val bufferIds = IntArray(1)
        GLES20.glGenBuffers(1, bufferIds, 0)
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            X_Y_COORDS_NUMBER * FLOAT_BYTE_SIZE * SQUARE_X_Y_COUNT,
            vertexBuffer,
            GLES20.GL_STATIC_DRAW
        )
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(
            0,
            2,
            GLES20.GL_FLOAT,
            false,
            FLOAT_BYTE_SIZE * X_Y_COORDS_NUMBER,
            0
        )
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        passThroughProgram = ProgramInfo.createProgram(
            context,
            R.raw.blur_pass_through_vertex_shader,
            R.raw.blur_pass_through_fragment_shader
        )
        guassianVerticalProgram = ProgramInfo.createProgram(
            context,
            R.raw.blur_pass_through_vertex_shader,
            R.raw.blur_gaussian_linear_vertex_fragment_shader
        )
        guassianHorizontalProgram = ProgramInfo.createProgram(
            context,
            R.raw.blur_pass_through_vertex_shader,
            R.raw.blur_gaussian_linear_horiz_fragment_shader
        )
    }

    companion object {
        private const val TAG = "BlurRenderer"
        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val SQUARE_X_Y_COUNT = 6
    }
}
