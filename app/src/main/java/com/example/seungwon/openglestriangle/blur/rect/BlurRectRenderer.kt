package com.example.seungwon.openglestriangle.blur.rect

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

class BlurRectRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val squareCoords: FloatArray = floatArrayOf(
        // X, Y
        -1f, -1f,
        1f, -1f,
        1f, 1f,
        -1f, 1f
    )
    private val txtCoords: FloatArray = floatArrayOf(
        // U, V
        0f, 1f,
        1f, 1f,
        1f, 0f,
        0f, 0f
    )
    private val rectSquareCoords: FloatArray = floatArrayOf(
        -0.5f, -0.5f,   // 0 bottom left
        0.5f, -0.5f,   // 1 bottom right
        -0.5f, 0.5f,   // 2 top left
        0.5f, 0.5f   // 3 top right
    )
    private val rectTxtCoords: FloatArray = floatArrayOf(
        0.25f, 0.75f,     // 2 top left
        0.75f, 0.75f,    // 3 top right
        0.25f, 0.25f,     // 0 bottom left
        0.75f, 0.25f     // 1 bottom right
    )
    private val vertexBuffer: FloatBuffer
    private val txtBuffer: FloatBuffer
    private val rectVertexBuffer: FloatBuffer
    private val rectTxtBuffer: FloatBuffer

    private val matrixView: FloatArray = FloatArray(16)
    private val projectionMatrix: FloatArray = FloatArray(16)

    private var originalRenderingProgram: Int = 0
    private var passThroughProgram: Int = 0

    private var textureId: Int = 0
    private var positionHandle: Int = 0
    private var mvpHandle: Int = 0
    private var textureHandle: Int = 0
    private var textureMvpHandle: Int = 0
    private var textureCoordsHandle: Int = 0

    private var originalTextureId: Int = 0
    private var originalPositionHandle: Int = 0
    private var originalTextureCoordsHandle: Int = 0
    private var originalMvpHandle: Int = 0

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
            .put(txtCoords)
        txtBuffer.position(0)

        rectVertexBuffer =
            ByteBuffer.allocateDirect(rectSquareCoords.size * FLOAT_BYTE_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(rectSquareCoords)
        rectVertexBuffer.position(0)

        rectTxtBuffer = ByteBuffer.allocateDirect(rectTxtCoords.size * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(rectTxtCoords)
        rectTxtBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        renderOriginalTexture()
        renderBlurredTexture()
    }

    private fun renderOriginalTexture() {
        Matrix.setIdentityM(projectionMatrix, 0)

        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, projectionMatrix, 0)

        GLES20.glEnableVertexAttribArray(originalPositionHandle)
        GLES20.glVertexAttribPointer(
            originalPositionHandle,
            X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(originalTextureCoordsHandle)
        GLES20.glVertexAttribPointer(
            originalTextureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER, GLES20.GL_FLOAT, false, 0, txtBuffer
        )

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, originalTextureId)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(originalPositionHandle)
        GLES20.glDisableVertexAttribArray(originalTextureCoordsHandle)
    }

    private fun renderBlurredTexture() {
        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(passThroughProgram)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, matrixView, 0)
        GLES20.glUniformMatrix4fv(textureMvpHandle, 1, false, matrixView, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false,
            X_Y_COORDS_NUMBER * FLOAT_BYTE_SIZE, rectVertexBuffer
        )
        GLES20.glEnableVertexAttribArray(textureCoordsHandle)
        GLES20.glVertexAttribPointer(
            textureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER,
            GLES20.GL_FLOAT,
            false,
            TEXTURE_COORDS_VERTEX_NUMBER * FLOAT_BYTE_SIZE,
            rectTxtBuffer
        )

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_STRIP,
            0,
            rectSquareCoords.size / X_Y_COORDS_NUMBER
        )

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordsHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // load original txt shaders
        originalRenderingProgram = ProgramInfo.createProgram(
            context,
            R.raw.simple_txt_vertex_shader,
            R.raw.simple_txt_fragment_shader
        )

        GLES20.glUseProgram(originalRenderingProgram)

        val originalBitmap = TxtLoaderUtil.getBitmap(context, R.drawable.park_dotori)
        originalTextureId = TxtLoaderUtil.getTxt(originalBitmap)

        originalPositionHandle = GLES20.glGetAttribLocation(originalRenderingProgram, "vPosition")
        originalTextureCoordsHandle =
            GLES20.glGetAttribLocation(originalRenderingProgram, "a_texCoord")
        originalMvpHandle = GLES20.glGetUniformLocation(originalRenderingProgram, "uMVPMatrix")

        // load blur txt shaders
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

        val bitmap = TxtLoaderUtil.getBitmap(context, R.drawable.park_dotori)
        val bitmapWidth = bitmap.width / 2
        textureId = TxtLoaderUtil.getTxt(bitmap)

//        GLES20.glUniform1f(texelHeightOffset, if (width == 0) 0f else BLUR_RATIO / height)
        GLES20.glUniform1f(
            texelWidthOffset,
            BLUR_RATIO / bitmapWidth.toFloat()
        )
    }

    companion object {
        private const val TAG = "BlurRectRenderer"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val SQUARE_X_Y_COUNT = 6
        private const val SQUARE_LINE_COUNT = 6
        private const val TEXTURE_COORDS_VERTEX_NUMBER = 2

        private const val BLUR_RATIO = 1f
    }
}
