package com.example.seungwon.openglestriangle.projection.translation

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.ShaderStatusInfo
import com.example.seungwon.openglestriangle.TextResourceReader
import com.example.seungwon.openglestriangle.util.TxtLoaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FilterRenderer(val context: Context) : GLSurfaceView.Renderer {
    private val squareAndTxtCoords: FloatArray = floatArrayOf(
            // X, Y, Z, U, V
            -1f, -1f, 0f, 1f,
            1f, -1f, 1f, 1f,
            1f, 1f, 1f, 0f,
            -1f, 1f, 0f, 0f
    )
    private val colorsCoords: FloatArray = floatArrayOf(
            // R, G, B, A
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f
    )
    private val matrixView = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private val vertexBuffer: FloatBuffer
    private val colorVertexBuffer: FloatBuffer

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var txtCoordHandle: Int = 0
    private var colorCoordHandle: Int = 0
    private var matrixHandle: Int = 0
    private var bitmapHandle: Int = 0

    init {
        vertexBuffer = ByteBuffer.allocateDirect(squareAndTxtCoords.size * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(squareAndTxtCoords)
        vertexBuffer.position(0)

        colorVertexBuffer = ByteBuffer.allocateDirect(colorsCoords.size * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(colorsCoords)
        colorVertexBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

//        Matrix.setIdentityM(matrixView, 0)

        vertexBuffer.position(0)
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer)

        vertexBuffer.position(COORDS_PER_VERTEX)
        txtCoordHandle = GLES20.glGetAttribLocation(program, "a_texCoord")
        GLES20.glEnableVertexAttribArray(txtCoordHandle)
        GLES20.glVertexAttribPointer(txtCoordHandle, TXT_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer)

//        colorCoordHandle = GLES20.glGetAttribLocation(program, "a_color")
//        GLES20.glEnableVertexAttribArray(colorCoordHandle)
//        GLES20.glVertexAttribPointer(colorCoordHandle, TXT_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, colorVertexBuffer)

        // matrixHandle will be used in orthoM for projection.
        matrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, projectionMatrix, 0)

        // OpenGL that future texture calls should be applied to this texture object
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(txtCoordHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val aspectRatio = if (width > height)
            width.toFloat() / height
        else
            height.toFloat() / width

        Log.d("StripRenderer", "onSurfaceChanged $aspectRatio")

        if (width >= height) {
            // Landscape
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            // Portrait or square
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -1.7f, 1.7f, -1f, 1f)
        }

        Matrix.setIdentityM(matrixView, 0)
//        Matrix.translateM(matrixView, 0, 0.2f, 0f, 0f)
//        Matrix.scaleM(matrixView, 0, 0.2f, 0f, 0f)
//        Matrix.rotateM(matrixView, 0, 270f, 0f, 1f, -1f)

        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, matrixView, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 0.0f)

        val vertextCodeString = TextResourceReader.readTextFileFromResource(context, R.raw.simple_txt_vertex_shader)
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertextCodeString)
        GLES20.glCompileShader(vertexShader)

        ShaderStatusInfo.getShaderStatus(vertexShader)

        val fragmentCodeString = TextResourceReader.readTextFileFromResource(context, R.raw.simple_txt_fragment_shader_with_filter_black_white)
        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentCodeString)
        GLES20.glCompileShader(fragmentShader)

        ShaderStatusInfo.getShaderStatus(fragmentShader)

        // Link verticesShader, fragmentShader to OpenGL
        program = GLES20.glCreateProgram()

        if (program == 0) {
            Log.w(TAG, "Could not create new program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)

        GLES20.glLinkProgram(program)
        GLES20.glUseProgram(program)

        val bitmap = TxtLoaderUtil.getBitmap(context, R.drawable.bogum)
        bitmapHandle = TxtLoaderUtil.getTxt(bitmap)

//        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
//                GLES20.GL_LINEAR.toFloat())
//        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
//                GLES20.GL_LINEAR.toFloat())
//        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
//                GLES20.GL_CLAMP_TO_EDGE)
//        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
//                GLES20.GL_CLAMP_TO_EDGE)

        bitmap.recycle()
    }

    companion object {
        private const val TAG = "StripRenderer"
        private const val COORDS_PER_VERTEX = 2
        private const val TXT_COORDS_PER_VERTEX = 2
        private const val FLOAT_SIZE_BYTES = 4
        private const val TRIANGLE_VERTICES_DATA_STRIDE_BYTES = (COORDS_PER_VERTEX + TXT_COORDS_PER_VERTEX) * FLOAT_SIZE_BYTES
    }
}
