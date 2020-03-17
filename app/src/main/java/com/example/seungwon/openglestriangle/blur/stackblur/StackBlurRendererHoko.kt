package com.example.seungwon.openglestriangle.blur.stackblur

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.seungwon.openglestriangle.ProgramInfo
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.TextResourceReader
import com.example.seungwon.openglestriangle.util.FrameBufferUtil
import com.example.seungwon.openglestriangle.util.TxtLoaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class StackBlurRendererHoko(private val context: Context) : GLSurfaceView.Renderer {
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
    private val vertexBuffer: FloatBuffer
    private val txtBuffer: FloatBuffer

    private val matrixView: FloatArray = FloatArray(16)

    private var passThroughProgram: Int = 0
    private var stackBlurProgram: Int = 0

    private var stackBlurPositionHandle: Int = 0
    private var stackBlurTextureHandle: Int = 0
    private var stackBlurTextureCoordsHandle: Int = 0
    private var stackBlurMvpHandle: Int = 0
    private var stackBlurTextureMvpHandle: Int = 0

    private var texelWidthOffset: Int = 0
    private var texelHeightOffset: Int = 0
    private var blurSizeHandle: Int = 0

    private var originalTextureId: Int = 0
    private var originalPositionHandle: Int = 0
    private var originalTextureCoordsHandle: Int = 0
    private var originalMvpHandle: Int = 0

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
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setIdentityM(matrixView, 0)

        // render scene to FBO A
        renderScene()

        // render FBO A to FBO B, using horizontal blur
        renderHorizontalBlur()

        // render FBO B to scene, using vertical blur
        renderVerticalBlur()
    }

    private fun renderVerticalBlur() {
        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(stackBlurProgram)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[1].textureId)

        GLES20.glUniform1f(texelHeightOffset, 0f)
        GLES20.glUniform1f(texelWidthOffset, BLUR_OFFSET)

        GLES20.glUniformMatrix4fv(stackBlurMvpHandle, 1, false, matrixView, 0)
        GLES20.glUniformMatrix4fv(stackBlurTextureMvpHandle, 1, false, matrixView, 0)

        GLES20.glEnableVertexAttribArray(stackBlurPositionHandle)
        GLES20.glVertexAttribPointer(
            stackBlurPositionHandle,
            X_Y_COORDS_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(stackBlurTextureCoordsHandle)
        GLES20.glVertexAttribPointer(
            stackBlurTextureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            txtBuffer
        )

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_FAN,
            0,
            squareCoords.size / X_Y_COORDS_NUMBER
        )

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(stackBlurPositionHandle)
        GLES20.glDisableVertexAttribArray(stackBlurTextureCoordsHandle)
    }

    private fun renderHorizontalBlur() {
        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(stackBlurProgram)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBuffers[1].frameBufferId)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[0].textureId)

        GLES20.glUniform1f(texelHeightOffset, BLUR_OFFSET)
        GLES20.glUniform1f(texelWidthOffset, 0f)

        GLES20.glUniformMatrix4fv(stackBlurMvpHandle, 1, false, matrixView, 0)
        GLES20.glUniformMatrix4fv(stackBlurTextureMvpHandle, 1, false, matrixView, 0)

        GLES20.glEnableVertexAttribArray(stackBlurPositionHandle)
        GLES20.glVertexAttribPointer(
            stackBlurPositionHandle,
            X_Y_COORDS_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(stackBlurTextureCoordsHandle)
        GLES20.glVertexAttribPointer(
            stackBlurTextureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            txtBuffer
        )

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_FAN,
            0,
            squareCoords.size / X_Y_COORDS_NUMBER
        )

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(stackBlurPositionHandle)
        GLES20.glDisableVertexAttribArray(stackBlurTextureCoordsHandle)
    }

    private fun renderScene() {
        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(passThroughProgram)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBuffers[0].frameBufferId)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, originalTextureId)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, matrixView, 0)

        GLES20.glEnableVertexAttribArray(originalPositionHandle)
        GLES20.glVertexAttribPointer(
            originalPositionHandle,
            X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false,
            0,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(originalTextureCoordsHandle)
        GLES20.glVertexAttribPointer(
            originalTextureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            txtBuffer
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(originalPositionHandle)
        GLES20.glDisableVertexAttribArray(originalTextureCoordsHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height

        textureFrameBuffers.add(FrameBufferUtil.createFrameTextureBuffer(width, height))
        textureFrameBuffers.add(FrameBufferUtil.createFrameTextureBuffer(width, height))

//        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // load shaders
        passThroughProgram = ProgramInfo.createProgram(
            context,
            R.raw.simple_txt_vertex_shader,
            R.raw.simple_txt_fragment_shader
        )

        GLES20.glUseProgram(passThroughProgram)

        val originalBitmap = TxtLoaderUtil.getBitmap(context, R.drawable.keyboard_test_2)
        originalTextureId = TxtLoaderUtil.getTxt(originalBitmap)

        originalPositionHandle =
            GLES20.glGetAttribLocation(passThroughProgram, "vPosition")
        originalTextureCoordsHandle =
            GLES20.glGetAttribLocation(passThroughProgram, "a_texCoord")
        originalMvpHandle = GLES20.glGetUniformLocation(passThroughProgram, "uMVPMatrix")

        // load gaussian shaders
        val boxblurShaderV = "shaders/stack_blur_hoko.vert"
        val boxlurShaderF = "shaders/box_blur.frag"//"shaders/stack_blur_hoko.frag"//
        val vertexShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            boxblurShaderV
        )
        val fragmentShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            boxlurShaderF
        )
        stackBlurProgram = ProgramInfo.createProgram(
            vertexShaderStr,
            fragmentShaderStr
        )
        GLES20.glUseProgram(stackBlurProgram)

        stackBlurPositionHandle = GLES20.glGetAttribLocation(stackBlurProgram, "aPosition")
        stackBlurTextureCoordsHandle = GLES20.glGetAttribLocation(stackBlurProgram, "aTextureCoord")

        stackBlurTextureHandle = GLES20.glGetUniformLocation(stackBlurProgram, "uTexture")

        stackBlurMvpHandle = GLES20.glGetUniformLocation(stackBlurProgram, "uMVPMatrix")
        stackBlurTextureMvpHandle = GLES20.glGetUniformLocation(stackBlurProgram, "uTexMatrix")

        texelWidthOffset = GLES20.glGetUniformLocation(stackBlurProgram, "uTexelWidthOffset")
        texelHeightOffset = GLES20.glGetUniformLocation(stackBlurProgram, "uTexelHeightOffset")
        blurSizeHandle = GLES20.glGetUniformLocation(stackBlurProgram, "uBlurSize")

        GLES20.glUniform1i(blurSizeHandle, 3)
    }

    companion object {
        private const val TAG = "StackBlurRenderer"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val SQUARE_X_Y_COUNT = 6
        private const val SQUARE_LINE_COUNT = 6
        private const val TEXTURE_COORDS_VERTEX_NUMBER = 2

        private const val BLUR_RATIO = 1f
        private const val BLUR_OFFSET = 0.00125f//1.3846153846f//1.3846153846f//0.003155048076953f
    }
}
