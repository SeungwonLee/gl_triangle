package com.example.seungwon.openglestriangle.text

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Environment
import android.util.Log
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.ShaderInfo
import com.example.seungwon.openglestriangle.TextResourceReader
import com.example.seungwon.openglestriangle.util.TxtLoaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class TextureRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val squareCoords: FloatArray = floatArrayOf(
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f // top right
    )
    private val txtCoords: FloatArray = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    )
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    private val vertexCount: Int = squareCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = squareCoords.size * COORDS_PER_VERTEX

    private val vertexBuffer: FloatBuffer
    private val txtBuffer: FloatBuffer
    private val drawOrderBuffer: ShortBuffer
    private val matrixView = FloatArray(16)

    private var program: Int = 0

    private var positionHandle: Int = 0
    private var txtHandle: Int = 0
    private var txtHandle2: Int = 0
    private var matrixHandle: Int = 0
    private var bitmapHandle: Int = 0

    private var width = 0
    private var height = 0
    private var captured = 0

    init {
        vertexBuffer = ByteBuffer.allocateDirect(squareCoords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)

        txtBuffer = ByteBuffer.allocateDirect(txtCoords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        txtBuffer.put(txtCoords)
        txtBuffer.position(0)

        drawOrderBuffer = ByteBuffer
                .allocateDirect(drawOrder.size * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
        drawOrderBuffer.put(drawOrder)
        drawOrderBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

        Matrix.setIdentityM(matrixView, 0)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        txtHandle = GLES20.glGetAttribLocation(program, "a_texCoord")
        GLES20.glEnableVertexAttribArray(txtHandle)
        GLES20.glVertexAttribPointer(txtHandle, TXT_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, txtBuffer)

        // matrixHandle will be used in orthoM for projection.
        matrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrixView, 0)

        // OpenGL that future texture calls should be applied to this texture object
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapHandle)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer)

        if (captured < 5) {
            TxtLoaderUtil.saveFrame(bitmapHandle, width, height)
            captured++
        }

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(txtHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 0.0f)

        val vertextCodeString = TextResourceReader.readTextFileFromResource(context, R.raw.simple_txt_vertex_shader)
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertextCodeString)
        GLES20.glCompileShader(vertexShader)

        ShaderInfo.getShaderStatus(vertexShader)

        val fragmentCodeString = TextResourceReader.readTextFileFromResource(context, R.raw.simple_txt_fragment_shader)
        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentCodeString)
        GLES20.glCompileShader(fragmentShader)

        ShaderInfo.getShaderStatus(fragmentShader)

        // Link verticesShader, fragmentShader to OpenGL
        program = GLES20.glCreateProgram()

        if (program == 0) {
            Log.w(TAG, "Could not create new program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)

        GLES20.glLinkProgram(program)
        GLES20.glUseProgram(program)

        val bitmap = TxtLoaderUtil.getBitmap(context, R.drawable.ic_500_800)
        bitmapHandle = TxtLoaderUtil.getTxt(bitmap)
        bitmap.recycle()
    }

    companion object {
        private const val TAG = "TextureRenderer"
        private const val COORDS_PER_VERTEX = 3
        private const val TXT_COORDS_PER_VERTEX = 2
    }
}