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

class BlurFrameBufferRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val squareCoords: FloatArray = floatArrayOf(
        -1.0f, -1.0f,   // 0 bottom left
        1.0f, -1.0f,   // 1 bottom right
        -1.0f, 1.0f,   // 2 top left
        1.0f, 1.0f   // 3 top right
    )
    private val txtCoords: FloatArray = floatArrayOf(
        0.0f, 1.0f,     // 2 top left
        1.0f, 1.0f,    // 3 top right
        0.0f, 0.0f,     // 0 bottom left
        1.0f, 0.0f     // 1 bottom right
    )
    private val vertexBuffer: FloatBuffer
    private val txtBuffer: FloatBuffer

    private val matrixView: FloatArray = FloatArray(16)

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
            .put(txtCoords)
        txtBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setIdentityM(matrixView, 0)
//
//        positionHandle = GLES20.glGetAttribLocation(passThroughProgram, "vPosition")
//        GLES20.glEnableVertexAttribArray(positionHandle)
//        GLES20.glVertexAttribPointer(
//            positionHandle, X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false,
//            0, vertexBuffer
//        )
//
//        textureCoordsHandle = GLES20.glGetAttribLocation(passThroughProgram, "a_texCoord")
//        GLES20.glEnableVertexAttribArray(textureCoordsHandle)
//        GLES20.glVertexAttribPointer(
//            textureCoordsHandle,
//            TEXTURE_COORDS_VERTEX_NUMBER,
//            GLES20.GL_FLOAT,
//            false,
//            0,
//            txtBuffer
//        )
//
//        // matrixHandle will be used in orthoM for projection.
//        mvpHandle = GLES20.glGetUniformLocation(passThroughProgram, "uMVPMatrix")
//        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, matrixView, 0)
//
//        // OpenGL that future texture calls should be applied to this texture object
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
//        GLES20.glDrawArrays(
//            GLES20.GL_TRIANGLE_STRIP,
//            0,
//            squareCoords.size / X_Y_COORDS_NUMBER
//        )

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, matrixView, 0)
        GLES20.glUniformMatrix4fv(textureMvpHandle, 1, false, matrixView, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false,
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

        // render scene to FBO A
        // render FBO A to FBO B, using horizontal blur
        // render FBO B to scene, using vertical blur

        val blurOffset = 0.003155048076953f
        // Draw single blurred image to FBO A
        GLES20.glUniform1f(texelHeightOffset, 0f)
        GLES20.glUniform1f(texelWidthOffset, blurOffset)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBuffers[0].frameBufferId)

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_STRIP,
            0,
            squareCoords.size / X_Y_COORDS_NUMBER
        )

        // Draw FBO A to FBO B
        GLES20.glUniform1f(texelHeightOffset, blurOffset)
        GLES20.glUniform1f(texelWidthOffset, 0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[0].textureId)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBuffers[1].frameBufferId)
        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_STRIP,
            0,
            squareCoords.size / X_Y_COORDS_NUMBER
        )

        // Draw FBO B to FBO A
        GLES20.glUniform1f(texelHeightOffset, blurOffset)
        GLES20.glUniform1f(texelWidthOffset, blurOffset)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[1].textureId)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBuffers[0].frameBufferId)
        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_STRIP,
            0,
            squareCoords.size / X_Y_COORDS_NUMBER
        )

        // Draw FBO A to main frame buffer
        GLES20.glUniform1f(texelHeightOffset, 0f)
        GLES20.glUniform1f(texelWidthOffset, 0f)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[0].textureId)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
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

        textureFrameBuffers.add(FrameBufferUtil.createFrameTextureBuffer(width, height))
        textureFrameBuffers.add(FrameBufferUtil.createFrameTextureBuffer(width, height))

//        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // load shaders
//        passThroughProgram = ProgramInfo.createProgram(
//            context,
//            R.raw.simple_txt_vertex_shader,
//            R.raw.simple_txt_fragment_shader
//        )
        val vertexShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            "shaders/blur_pass_through_vertex_shader.glsl"
        )
        val fragmentShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            "shaders/blur_pass_through_fragment_shader.glsl"
        )
        passThroughProgram = ProgramInfo.createProgram(
            vertexShaderStr,
            fragmentShaderStr
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
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        textureId = TxtLoaderUtil.getTxt(bitmap)

        GLES20.glUniform1f(texelHeightOffset, BLUR_RATIO / /*bitmapHeight.toFloat()*/300f)
        GLES20.glUniform1f(
            texelWidthOffset,
            BLUR_RATIO / /*bitmapWidth.toFloat()*/300f
        )
    }

    companion object {
        private const val TAG = "BlurRenderer"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val SQUARE_X_Y_COUNT = 6
        private const val SQUARE_LINE_COUNT = 6
        private const val TEXTURE_COORDS_VERTEX_NUMBER = 2

        private const val BLUR_RATIO = 1f
    }
}