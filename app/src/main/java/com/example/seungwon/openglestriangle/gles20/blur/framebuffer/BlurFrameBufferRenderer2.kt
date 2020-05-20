package com.example.seungwon.openglestriangle.gles20.blur.framebuffer

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

class BlurFrameBufferRenderer2(private val context: Context) : GLSurfaceView.Renderer {
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
    private var guassianProgram: Int = 0

    private var gaussianPositionHandle: Int = 0
    private var gaussianMvpHandle: Int = 0
    private var gaussianTextureHandle: Int = 0
    private var gaussianTextureMvpHandle: Int = 0
    private var gaussianTextureCoordsHandle: Int = 0

    private var texelWidthOffset: Int = 0
    private var texelHeightOffset: Int = 0

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

        GLES20.glUseProgram(guassianProgram)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[1].textureId)

        GLES20.glUniform1f(texelHeightOffset, 0f)
        GLES20.glUniform1f(texelWidthOffset, BLUR_OFFSET)

        GLES20.glUniformMatrix4fv(gaussianMvpHandle, 1, false, matrixView, 0)
        GLES20.glUniformMatrix4fv(gaussianTextureMvpHandle, 1, false, matrixView, 0)

        GLES20.glEnableVertexAttribArray(gaussianPositionHandle)
        GLES20.glVertexAttribPointer(
            gaussianPositionHandle,
            X_Y_COORDS_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(gaussianTextureCoordsHandle)
        GLES20.glVertexAttribPointer(
            gaussianTextureCoordsHandle,
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
        GLES20.glDisableVertexAttribArray(gaussianPositionHandle)
        GLES20.glDisableVertexAttribArray(gaussianTextureCoordsHandle)
    }

    private fun renderHorizontalBlur() {
        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(guassianProgram)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBuffers[1].frameBufferId)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[0].textureId)

        GLES20.glUniform1f(texelHeightOffset, BLUR_OFFSET)
        GLES20.glUniform1f(texelWidthOffset, 0f)

        GLES20.glUniformMatrix4fv(gaussianMvpHandle, 1, false, matrixView, 0)
        GLES20.glUniformMatrix4fv(gaussianTextureMvpHandle, 1, false, matrixView, 0)

        GLES20.glEnableVertexAttribArray(gaussianPositionHandle)
        GLES20.glVertexAttribPointer(
            gaussianPositionHandle,
            X_Y_COORDS_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(gaussianTextureCoordsHandle)
        GLES20.glVertexAttribPointer(
            gaussianTextureCoordsHandle,
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
        GLES20.glDisableVertexAttribArray(gaussianPositionHandle)
        GLES20.glDisableVertexAttribArray(gaussianTextureCoordsHandle)
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
        val vertexShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            "shaders/blur_pass_through_vertex_shader.glsl"
        )
        val fragmentShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            "shaders/blur_pass_through_fragment_shader.glsl"
        )
        guassianProgram = ProgramInfo.createProgram(
            vertexShaderStr,
            fragmentShaderStr
        )
        GLES20.glUseProgram(guassianProgram)

        gaussianPositionHandle = GLES20.glGetAttribLocation(guassianProgram, "aPosition")
        gaussianTextureCoordsHandle = GLES20.glGetAttribLocation(guassianProgram, "aTextureCoord")

        gaussianTextureHandle = GLES20.glGetUniformLocation(guassianProgram, "uTexture")

        gaussianMvpHandle = GLES20.glGetUniformLocation(guassianProgram, "uMVPMatrix")
        gaussianTextureMvpHandle = GLES20.glGetUniformLocation(guassianProgram, "uTexMatrix")

        texelWidthOffset = GLES20.glGetUniformLocation(guassianProgram, "uTexelWidthOffset")
        texelHeightOffset = GLES20.glGetUniformLocation(guassianProgram, "uTexelHeightOffset")
    }

    companion object {
        private const val TAG = "BlurRendererFBO2"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val SQUARE_X_Y_COUNT = 6
        private const val SQUARE_LINE_COUNT = 6
        private const val TEXTURE_COORDS_VERTEX_NUMBER = 2

        private const val BLUR_RATIO = 1f
        private const val BLUR_OFFSET = 0.002f//1.3846153846f//1.3846153846f//0.003155048076953f
    }
}