package com.example.seungwon.openglestriangle.gles20.blur.rect

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
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

class BlurRectRenderer2(private val context: Context) : GLSurfaceView.Renderer {
    private val squareCoords: FloatArray = floatArrayOf(
        // X, Y
        -1f, -1f,
        1f, -1f,
        1f, 1f,
        -1f, 1f
    )
    private val squareHalfCoords: FloatArray = floatArrayOf(
        // X, Y
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f
    )
    private val txtCoords: FloatArray = floatArrayOf(
        // U, V
        0f, 1f,
        1f, 1f,
        1f, 0f,
        0f, 0f
    )

    private val txtCoordsYFlip: FloatArray = floatArrayOf(
        // U, V
        0f, 0f,
        1f, 0f,
        1f, 1f,
        0f, 1f
    )

    private val halfVertexBuffer: FloatBuffer
    private val vertexBuffer: FloatBuffer
    private val txtBuffer: FloatBuffer
    private val txtBufferYFlip: FloatBuffer
    private val rectVertexCoordsList: ArrayList<FloatArray> = ArrayList()
    private val rectTxtCoordsList: ArrayList<FloatArray> = ArrayList()
    private val rectVertexBuffer: FloatBuffer
    private val rectTxtBuffer: FloatBuffer

    private val matrixView: FloatArray = FloatArray(16)

    private val scaledTxtMatrixView: FloatArray = FloatArray(16)
    private val scaledVertexMatrix: FloatArray = FloatArray(16)
    private val projectionMatrix: FloatArray = FloatArray(16)

    private var originalRenderingProgram: Int = 0
    private var gaussianBlurProgram: Int = 0

    private var positionHandle: Int = 0
    private var mvpHandle: Int = 0
    private var textureHandle: Int = 0
    private var textureMvpHandle: Int = 0
    private var textureCoordsHandle: Int = 0
    private var blurProjectionMatrixHandle: Int = 0

    private var originalTextureId: Int = 0
    private var originalPositionHandle: Int = 0
    private var originalTextureCoordsHandle: Int = 0
    private var originalMvpHandle: Int = 0
    private var originalTextureMvpHandle: Int = 0
    private var originalProjectionMatrixHandle: Int = 0

    private var texelWidthOffset: Int = 0
    private var texelHeightOffset: Int = 0

    private var width: Int = 0
    private var height: Int = 0

    private var textureFrameBufferA: FrameBufferUtil.TextureFrameBuffer? = null
    private var textureFrameBufferB: FrameBufferUtil.TextureFrameBuffer? = null

    var blurOffset: Float = 0f
    var loopCount = 1

    @Volatile
    var rectStartPointX: Float = 0f

    @Volatile
    var rectStartPointY: Float = 0f


    init {
        halfVertexBuffer =
            ByteBuffer.allocateDirect(squareHalfCoords.size * FLOAT_BYTE_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(squareHalfCoords)
        halfVertexBuffer.position(0)

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

        txtBufferYFlip = ByteBuffer.allocateDirect(txtCoordsYFlip.size * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(txtCoordsYFlip)
        txtBufferYFlip.position(0)

        rectVertexBuffer = ByteBuffer.allocateDirect(txtCoords.size * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        rectTxtBuffer = ByteBuffer.allocateDirect(txtCoords.size * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        renderOriginalTexture()

        rectVertexCoordsList.forEachIndexed { index, floats ->
            rectVertexBuffer.clear()
            rectVertexBuffer.put(floats)
            rectVertexBuffer.position(0)

            rectTxtBuffer.clear()
            rectTxtBuffer.put(rectTxtCoordsList[index])
            rectTxtBuffer.position(0)

            // render scene to FBO A
            renderScene(
                halfVertexBuffer,
                txtBufferYFlip,
                textureFrameBufferA?.frameBufferId ?: 0,
                originalTextureId
            )

            for (i in 0 until loopCount) {
                // render FBO A to FBO B, using horizontal blur
                renderHorizontalBlur(vertexBuffer, txtBufferYFlip)

                // render FBO B to scene, using vertical blur
                renderVerticalBlur(vertexBuffer, txtBufferYFlip)
            }

            renderScene2(
                rectVertexBuffer,
                rectTxtBuffer,
                0,
                textureFrameBufferA?.textureId ?: 0
            )
        }
    }

    private fun renderOriginalTexture() {
        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, scaledVertexMatrix, 0)
        GLES20.glUniformMatrix4fv(originalTextureMvpHandle, 1, false, scaledTxtMatrixView, 0)
        GLES20.glUniformMatrix4fv(originalProjectionMatrixHandle, 1, false, projectionMatrix, 0)

        GLES20.glEnableVertexAttribArray(originalPositionHandle)
        GLES20.glVertexAttribPointer(
            originalPositionHandle,
            X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false, 0, halfVertexBuffer
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

    private fun renderVerticalBlur(vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        val textureFrameBufferA = textureFrameBufferA ?: error("textureFrameBufferA null")
        val textureFrameBufferB = textureFrameBufferB ?: error("textureFrameBufferB null")
        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(gaussianBlurProgram)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBufferA.frameBufferId)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBufferB.textureId)

        GLES20.glUniform1f(texelHeightOffset, 0f)
        GLES20.glUniform1f(texelWidthOffset, blurOffset / (width / WIDTH_DIVIDER))

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, projectionMatrix, 0)
        GLES20.glUniformMatrix4fv(textureMvpHandle, 1, false, projectionMatrix, 0)
        GLES20.glUniformMatrix4fv(blurProjectionMatrixHandle, 1, false, projectionMatrix, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            X_Y_COORDS_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(textureCoordsHandle)
        GLES20.glVertexAttribPointer(
            textureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            textureBuffer
        )

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_FAN,
            0,
            squareCoords.size / X_Y_COORDS_NUMBER
        )

//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordsHandle)
    }

    private fun renderHorizontalBlur(vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        val textureFrameBufferA = textureFrameBufferA ?: error("textureFrameBufferA null")
        val textureFrameBufferB = textureFrameBufferB ?: error("textureFrameBufferB null")
//        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(gaussianBlurProgram)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBufferB.frameBufferId)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBufferA.textureId)

        GLES20.glUniform1f(texelHeightOffset, blurOffset / (height / WIDTH_DIVIDER))
        GLES20.glUniform1f(texelWidthOffset, 0f)

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, projectionMatrix, 0)
        GLES20.glUniformMatrix4fv(textureMvpHandle, 1, false, projectionMatrix, 0)
        GLES20.glUniformMatrix4fv(blurProjectionMatrixHandle, 1, false, projectionMatrix, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            X_Y_COORDS_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(textureCoordsHandle)
        GLES20.glVertexAttribPointer(
            textureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            textureBuffer
        )

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_FAN,
            0,
            squareCoords.size / X_Y_COORDS_NUMBER
        )

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordsHandle)
    }

    private fun renderScene(
        vertexBuffer: FloatBuffer,
        textureBuffer: FloatBuffer,
        frameBufferId: Int,
        textureId: Int
    ) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId)

        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, scaledVertexMatrix, 0)
        GLES20.glUniformMatrix4fv(originalTextureMvpHandle, 1, false, scaledTxtMatrixView, 0)
        GLES20.glUniformMatrix4fv(originalProjectionMatrixHandle, 1, false, projectionMatrix, 0)

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
            textureBuffer
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        if (frameBufferId != 0) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(originalPositionHandle)
        GLES20.glDisableVertexAttribArray(originalTextureCoordsHandle)
    }

    private fun renderScene2(
        vertexBuffer: FloatBuffer,
        textureBuffer: FloatBuffer,
        frameBufferId: Int,
        textureId: Int
    ) {
        val matrix = FloatArray(16)
        Matrix.setIdentityM(matrix, 0)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId)

        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, matrix, 0)
        GLES20.glUniformMatrix4fv(originalTextureMvpHandle, 1, false, matrix, 0)
        GLES20.glUniformMatrix4fv(originalProjectionMatrixHandle, 1, false, matrix, 0)

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
            textureBuffer
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        if (frameBufferId != 0) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(originalPositionHandle)
        GLES20.glDisableVertexAttribArray(originalTextureCoordsHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height

        Log.d(TAG, "onSurfaceChanged $width $height")
        textureFrameBufferA = FrameBufferUtil.createFrameTextureBuffer(
            width,
            height
        )
        textureFrameBufferB = FrameBufferUtil.createFrameTextureBuffer(
            width,
            height
        )
        GLES20.glViewport(0, 0, width, height)

        Matrix.setIdentityM(scaledVertexMatrix, 0)
        Matrix.setIdentityM(scaledTxtMatrixView, 0)
        Matrix.setIdentityM(projectionMatrix, 0)

        val temp2 = FloatArray(16)
        Matrix.setIdentityM(temp2, 0)
        Matrix.scaleM(temp2, 0, 1000f, 2464f, 0f)
//        Matrix.scaleM(scaledTxtMatrixView, 0,1440f, 2464f, 0f)

        val temp = FloatArray(16)
        Matrix.setIdentityM(temp, 0)
//        Matrix.orthoM(projectionMatrix, 0, /*width * -0.5f*/-1f, /*width * 0.5f*/1f, /*height * -0.5f*/-1f, /*height * 0.5f*/1f, -1f, 1f)
        Matrix.orthoM(
            temp,
            0,
            width * -0.5f,
            width * 0.5f,
            height * -0.5f,
            height * 0.5f,
            -1f,
            1f
        )

        Matrix.multiplyMM(scaledVertexMatrix, 0, temp2, 0, temp, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // load original txt shaders
        originalRenderingProgram = ProgramInfo.createProgram(
            context,
            R.raw.simple_txt_vertex_shader_with_matrix,
            R.raw.simple_txt_fragment_shader
        )

        GLES20.glUseProgram(originalRenderingProgram)

        val originalBitmap = TxtLoaderUtil.getBitmap(context, R.drawable.park_dotori)
        originalTextureId = TxtLoaderUtil.getTxt(originalBitmap)

        originalPositionHandle =
            GLES20.glGetAttribLocation(originalRenderingProgram, "vPosition")
        originalTextureCoordsHandle =
            GLES20.glGetAttribLocation(originalRenderingProgram, "a_texCoord")
        originalMvpHandle = GLES20.glGetUniformLocation(originalRenderingProgram, "uMVPMatrix")
        originalTextureMvpHandle =
            GLES20.glGetUniformLocation(originalRenderingProgram, "uTextureMatrix")
        originalProjectionMatrixHandle =
            GLES20.glGetUniformLocation(originalRenderingProgram, "uProjectionMatrix")

        // load blur txt shaders
        val vertexShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            "shaders/blur_pass_through_vertex_shader.glsl"
        )
        val fragmentShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            "shaders/blur_pass_through_fragment_shader.glsl"
        )
        gaussianBlurProgram = ProgramInfo.createProgram(
            vertexShaderStr,
            fragmentShaderStr
        )

        GLES20.glUseProgram(gaussianBlurProgram)

        positionHandle = GLES20.glGetAttribLocation(gaussianBlurProgram, "aPosition")
        textureCoordsHandle = GLES20.glGetAttribLocation(gaussianBlurProgram, "aTextureCoord")

        textureHandle = GLES20.glGetUniformLocation(gaussianBlurProgram, "uTexture")

        mvpHandle = GLES20.glGetUniformLocation(gaussianBlurProgram, "uMVPMatrix")
        textureMvpHandle = GLES20.glGetUniformLocation(gaussianBlurProgram, "uTexMatrix")
//        blurProjectionMatrixHandle =
//            GLES20.glGetUniformLocation(gaussianBlurProgram, "uProjectionMatrix")

        texelWidthOffset = GLES20.glGetUniformLocation(gaussianBlurProgram, "uTexelWidthOffset")
        texelHeightOffset =
            GLES20.glGetUniformLocation(gaussianBlurProgram, "uTexelHeightOffset")


//        GLES20.glUniform1f(texelHeightOffset, BLUR_RATIO / /*bitmapWidth*/700f)
//        GLES20.glUniform1f(
//            texelWidthOffset,
//            BLUR_RATIO / /*bitmapWidth*/700f
//        )
    }

    fun onDrawBlurRect(endPointX: Float, endPointY: Float, postAction: () -> Unit): Boolean {
        Log.d(TAG, "onTouch")
        if (width == 0 || height == 0) {
            Log.e(TAG, "onTouch $width $height == 0")
            return false
        }

        rectTxtCoordsList.add(
            getTextureCoords(
                rectStartPointX,
                rectStartPointY,
                endPointX,
                endPointY
            )
        )

        rectVertexCoordsList.add(
            getVertexCoords(
                rectStartPointX,
                rectStartPointY,
                endPointX,
                endPointY
            )
        )

        postAction.invoke()

        return true
    }

    private fun getTextureCoords(
        rectStartPointX: Float,
        rectStartPointY: Float,
        endPointX: Float,
        endPointY: Float
    ): FloatArray {

//        val rectLengthPixelX = (endPointX - rectStartPointX)
//        val rectLengthPixelY = (endPointY - rectStartPointY)

        Log.d(TAG, "onTouch rectStartPointX $rectStartPointX rectStartPointY $rectStartPointY")
        Log.d(TAG, "onTouch rectLengthPixelX $endPointX rectLengthPixelY $endPointY")

        val texturePointX = rectStartPointX / width
        val texturePointY = rectStartPointY / height
        var rectangleTextureX = endPointX / width//texturePointX + (rectLengthPixelX / width)
        var rectangleTextureY = endPointY / height//texturePointY + (rectLengthPixelY / height)
        val textureCoords = FloatArray(8)

//        0f, 1f,
//        1f, 1f,
//        1f, 0f,
//        0f, 0f

        if (rectangleTextureX > 1) {
            rectangleTextureX = 1f
        }
        if (rectangleTextureX < -1) {
            rectangleTextureX = -1f
        }

        if (rectangleTextureY > 1) {
            rectangleTextureY = 1f
        }
        if (rectangleTextureY < -1) {
            rectangleTextureY = -1f
        }

        textureCoords[0] = texturePointX
        textureCoords[1] = rectangleTextureY
        textureCoords[2] = rectangleTextureX
        textureCoords[3] = rectangleTextureY
        textureCoords[4] = rectangleTextureX
        textureCoords[5] = texturePointY
        textureCoords[6] = texturePointX
        textureCoords[7] = texturePointY

//        textureCoords[0] = 0f
//        textureCoords[1] = 1f
//        textureCoords[2] = 1f
//        textureCoords[3] = 1f
//        textureCoords[4] = 1f
//        textureCoords[5] = 0f
//        textureCoords[6] = 0f
//        textureCoords[7] = 0f

        textureCoords.forEach {
            Log.d(TAG, "onTouch foreach $it ")
        }

        return textureCoords
    }

    private fun getVertexCoords(
        rectStartPointX: Float,
        rectStartPointY: Float,
        endPointX: Float,
        endPointY: Float
    ): FloatArray {
        val startPointX = getNomalizedGlCoordsX(rectStartPointX, width)
        val startPointY = getNomalizedGlCoordsY(rectStartPointY, height)
        var rectX = getNomalizedGlCoordsX(endPointX, width)
        var rectY = getNomalizedGlCoordsY(endPointY, height)

        if (rectX > 1) {
            rectX = 1f
        }
        if (rectX < -1) {
            rectX = -1f
        }

        if (rectY > 1) {
            rectY = 1f
        }
        if (rectY < -1) {
            rectY = -1f
        }

//        Log.d(TAG, "onTouch rectX $rectX rectY $rectY")

//        private val squareCoords: FloatArray = floatArrayOf(
//            // X, Y
//            -1f, -1f,
//            1f, -1f,
//            1f, 1f,
//            -1f, 1f
//        )

//        vertextCoords[0] = -1f
//        vertextCoords[1] = -1f
//        vertextCoords[2] = 1f
//        vertextCoords[3] = -1f
//        vertextCoords[4] = 1f
//        vertextCoords[5] = 1f
//        vertextCoords[6] = -1f
//        vertextCoords[7] = 1f

        val vertextCoords = FloatArray(8)
        vertextCoords[0] = startPointX
        vertextCoords[1] = rectY
        vertextCoords[2] = rectX
        vertextCoords[3] = rectY
        vertextCoords[4] = rectX
        vertextCoords[5] = startPointY
        vertextCoords[6] = startPointX
        vertextCoords[7] = startPointY

//        for (i in vertextCoords.indices) {
//            vertextCoords[i] = vertextCoords[i] * 0.5f
//        }

        return vertextCoords
    }

    private fun getNomalizedGlCoordsX(x: Float, screenWidth: Int): Float =
        x / screenWidth * 2.0f - 1.0f

    private fun getNomalizedGlCoordsY(y: Float, screenHeight: Int): Float =
        y / screenHeight * -2.0f + 1.0f

    companion object {
        private const val TAG = "BlurRectRenderer"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val SQUARE_X_Y_COUNT = 6
        private const val SQUARE_LINE_COUNT = 6
        private const val TEXTURE_COORDS_VERTEX_NUMBER = 2
        private const val VERTEX_RESOLUTION = 300f
        private const val TEXTURE_RESOLUTION = 0.5f

        private const val BLUR_RATIO = 1f
        private const val BLUR_OFFSET = 0.003f//0.00001f
        private const val WIDTH_DIVIDER = 3f
        //0.003f//0.002f//1.3846153846f//1.3846153846f//0.003155048076953f
    }
}
