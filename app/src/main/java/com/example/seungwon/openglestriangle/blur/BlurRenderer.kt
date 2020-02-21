package com.example.seungwon.openglestriangle.blur

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.seungwon.openglestriangle.ProgramInfo
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.util.FrameBufferUtil
import com.example.seungwon.openglestriangle.util.TxtLoaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BlurRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val squareCoords: FloatArray = floatArrayOf(
        -1.0f, -1.0f,   // 0 bottom left
        1.0f, -1.0f,   // 1 bottom right
        -1.0f, 1.0f,   // 2 top left
        1.0f, 1.0f   // 3 top right
    )
    private val txtCoords: FloatArray = floatArrayOf(
        0.0f, 0.0f,     // 0 bottom left
        1.0f, 0.0f,     // 1 bottom right
        0.0f, 1.0f,     // 2 top left
        1.0f, 1.0f    // 3 top right
    )
    private val vertexBuffer: FloatBuffer
    private val txtBuffer: FloatBuffer

    private var passThroughProgram: Int = 0
    private var guassianVerticalProgram: Int = 0
    private var guassianHorizontalProgram: Int = 0

    private var textureId: Int = 0
    private var positionHandle: Int = 0
    private var mvpHandle: Int = 0
    private var textureHandle: Int = 0
    private var textureMvpHandle: Int = 0
    private var textureCoordsHandle: Int = 0

    private var texelWidthOffset: Int = 0
    private var texelHeightOffset: Int = 0

    private var width: Int = 0
    private var height: Int = 0

    private val textureFrameBuffers: ArrayList<FrameBufferUtil.TextureFrameBuffer> = ArrayList()

    init {
        vertexBuffer =
            ByteBuffer.allocateDirect(squareCoords.size * FLOAT_BYTE_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(squareCoords)
        vertexBuffer.position(0)

        txtBuffer = ByteBuffer.allocateDirect(txtCoords.size * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        txtBuffer.put(txtCoords)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        val identityMatrix = FloatArray(16)
        Matrix.setIdentityM(identityMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, identityMatrix, 0)
        GLES20.glUniformMatrix4fv(textureMvpHandle, 1, false, identityMatrix, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false,
            X_Y_COORDS_NUMBER * FLOAT_BYTE_SIZE, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(textureCoordsHandle)
        GLES20.glVertexAttribPointer(
            textureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER,
            GLES20.GL_FLOAT,
            false,
            TEXTURE_COORDS_VERTEX_NUMBER * FLOAT_BYTE_SIZE,
            txtBuffer
        )

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_STRIP,
            0,
            squareCoords.size / X_Y_COORDS_NUMBER
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordsHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // load shaders
        passThroughProgram = ProgramInfo.createProgram(
            context,
            R.raw.blur_pass_through_vertex_shader,
            R.raw.blur_pass_through_fragment_shader
        )
        GLES20.glUseProgram(passThroughProgram)

        positionHandle = GLES20.glGetAttribLocation(passThroughProgram, "aPosition")
        textureCoordsHandle = GLES20.glGetAttribLocation(passThroughProgram, "aTextureCoord")

        textureHandle = GLES20.glGetUniformLocation(passThroughProgram, "uTexture")

        mvpHandle = GLES20.glGetUniformLocation(passThroughProgram, "uMVPMatrix")
        textureMvpHandle = GLES20.glGetUniformLocation(passThroughProgram, "uTexMatrix")

        texelWidthOffset = GLES20.glGetUniformLocation(passThroughProgram, "uTexelWidthOffset")
        texelHeightOffset = GLES20.glGetUniformLocation(passThroughProgram, "uTexelHeightOffset")

        val bitmap = TxtLoaderUtil.getBitmap(context, R.drawable.bogum)
        textureId = TxtLoaderUtil.getTxt(bitmap)

//        GLES20.glUniform1f(texelHeightOffset, if (width == 0) 0f else BLUR_RATIO / height)
        GLES20.glUniform1f(texelWidthOffset, /*if (width == 0) 0f else BLUR_RATIO / width*/0f)
    }

    companion object {
        private const val TAG = "BlurRenderer"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val SQUARE_X_Y_COUNT = 6
        private const val SQUARE_LINE_COUNT = 6
        private const val TEXTURE_COORDS_VERTEX_NUMBER = 2

        private const val BLUR_RATIO = 0.1f
    }
}
