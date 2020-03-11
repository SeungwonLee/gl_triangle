package com.example.seungwon.openglestriangle.mosaic.rect

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.seungwon.openglestriangle.ProgramInfo
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.util.FrameBufferUtil
import com.example.seungwon.openglestriangle.util.TxtLoaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MosaicRectRenderer(private val context: Context) : GLSurfaceView.Renderer {
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

    private val rectVertexBuffer: FloatBuffer
    private val rectTxtBuffer: FloatBuffer

    private val rectVertexCoordsList: ArrayList<FloatArray> = ArrayList()
    private val rectTxtCoordsList: ArrayList<FloatArray> = ArrayList()

    private val matrixView: FloatArray = FloatArray(16)
    private val projectionMatrix: FloatArray = FloatArray(16)

    private var originalRenderingProgram: Int = 0
    private var mosaicProgram: Int = 0

    private var mosaicPositionHandle: Int = 0
    private var mosaicMvpHandle: Int = 0
    private var mosaicTextureHandle: Int = 0
    private var mosaicTextureCoordsHandle: Int = 0

    private var originalTextureId: Int = 0
    private var originalPositionHandle: Int = 0
    private var originalTextureCoordsHandle: Int = 0
    private var originalMvpHandle: Int = 0

    private var width: Int = 0
    private var height: Int = 0

    var rectStartPointX: Float = 0f
    var rectStartPointY: Float = 0f

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

        rectVertexBuffer = ByteBuffer.allocateDirect(txtCoords.size * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        rectTxtBuffer = ByteBuffer.allocateDirect(txtCoords.size * FLOAT_BYTE_SIZE)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setIdentityM(matrixView, 0)

        // render scene
        renderOriginalTexture()

        // TODO Thread issue
        rectVertexCoordsList.forEachIndexed { index, floats ->
            rectVertexBuffer.clear()
            rectVertexBuffer.put(floats)
            rectVertexBuffer.position(0)

            rectTxtBuffer.clear()
            rectTxtBuffer.put(rectTxtCoordsList[index])
            rectTxtBuffer.position(0)

            // render mosaic rect
            renderMosaicRect(rectVertexBuffer, rectTxtBuffer)
        }
    }

    fun onDrawBlurRect(endPointX: Float, endPointY: Float, postAction: () -> Unit): Boolean {
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

        val rectLengthPixelX = (endPointX - rectStartPointX)
        val rectLengthPixelY = (endPointY - rectStartPointY)

        Log.d(
            TAG,
            "onTouch rectStartPointX $rectStartPointX rectStartPointY $rectStartPointY"
        )
        Log.d(
            TAG,
            "onTouch rectLengthPixelX $rectLengthPixelX rectLengthPixelY $rectLengthPixelY"
        )

        val texturePointX = rectStartPointX / width
        val texturePointY = rectStartPointY / height
        var rectangleTextureX = texturePointX + (rectLengthPixelX / width)
        var rectangleTextureY = texturePointY + (rectLengthPixelY / height)
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

//        textureCoords[0] = texturePointX
//        textureCoords[1] = rectangleTextureY
//        textureCoords[2] = rectangleTextureX
//        textureCoords[3] = rectangleTextureY
//        textureCoords[4] = rectangleTextureX
//        textureCoords[5] = texturePointY
//        textureCoords[6] = texturePointX
//        textureCoords[7] = texturePointY

        textureCoords[0] = 0f
        textureCoords[1] = 1f
        textureCoords[2] = 1f
        textureCoords[3] = 1f
        textureCoords[4] = 1f
        textureCoords[5] = 0f
        textureCoords[6] = 0f
        textureCoords[7] = 0f

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

        Log.d(TAG, "onTouch rectX $rectX rectY $rectY")

//        private val squareCoords: FloatArray = floatArrayOf(
//            // X, Y
//            -1f, -1f,
//            1f, -1f,
//            1f, 1f,
//            -1f, 1f
//        )
        val vertextCoords = FloatArray(8)

        vertextCoords[0] = -1f
        vertextCoords[1] = -1f
        vertextCoords[2] = 1f
        vertextCoords[3] = -1f
        vertextCoords[4] = 1f
        vertextCoords[5] = 1f
        vertextCoords[6] = -1f
        vertextCoords[7] = 1f

//        vertextCoords[0] = startPointX
//        vertextCoords[1] = rectY
//        vertextCoords[2] = rectX
//        vertextCoords[3] = rectY
//        vertextCoords[4] = rectX
//        vertextCoords[5] = startPointY
//        vertextCoords[6] = startPointX
//        vertextCoords[7] = startPointY

        return vertextCoords
    }

    private fun renderMosaicRect(vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(mosaicProgram)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, originalTextureId)

        GLES20.glUniformMatrix4fv(mosaicMvpHandle, 1, false, matrixView, 0)

        GLES20.glEnableVertexAttribArray(mosaicPositionHandle)
        GLES20.glVertexAttribPointer(
            mosaicPositionHandle,
            X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false,
            0,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(mosaicTextureCoordsHandle)
        GLES20.glVertexAttribPointer(
            mosaicTextureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER,
            GLES20.GL_FLOAT,
            false,
            0,
            textureBuffer
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(mosaicPositionHandle)
        GLES20.glDisableVertexAttribArray(mosaicTextureCoordsHandle)
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

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height

//        textureFrameBuffers.add(FrameBufferUtil.createFrameTextureBuffer(width, height))
//        textureFrameBuffers.add(FrameBufferUtil.createFrameTextureBuffer(width, height))

//        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // load original txt shaders
        originalRenderingProgram = ProgramInfo.createProgram(
            context,
            R.raw.simple_txt_vertex_shader,
            R.raw.simple_txt_fragment_shader
        )

        GLES20.glUseProgram(originalRenderingProgram)

        val originalBitmap = TxtLoaderUtil.getBitmap(context, R.drawable.keyboard_test_2)
        originalTextureId = TxtLoaderUtil.getTxt(originalBitmap)
        originalPositionHandle =
            GLES20.glGetAttribLocation(originalRenderingProgram, "vPosition")
        originalTextureCoordsHandle =
            GLES20.glGetAttribLocation(originalRenderingProgram, "a_texCoord")
        originalMvpHandle = GLES20.glGetUniformLocation(originalRenderingProgram, "uMVPMatrix")

        // load mosaic shaders
        mosaicProgram = ProgramInfo.createProgram(
            context,
            R.raw.mosaic_vertex_shader,
            R.raw.mosaic_fragment_shader
        )

        GLES20.glUseProgram(mosaicProgram)
        mosaicPositionHandle = GLES20.glGetAttribLocation(mosaicProgram, "vPosition")
        mosaicTextureCoordsHandle = GLES20.glGetAttribLocation(mosaicProgram, "a_texCoord")
        mosaicTextureHandle = GLES20.glGetUniformLocation(mosaicProgram, "uTexture")
        mosaicMvpHandle = GLES20.glGetUniformLocation(mosaicProgram, "uMVPMatrix")
    }

    private fun getNomalizedGlCoordsX(x: Float, screenWidth: Int): Float =
        x / screenWidth * 2.0f - 1.0f

    private fun getNomalizedGlCoordsY(y: Float, screenHeight: Int): Float =
        y / screenHeight * -2.0f + 1.0f

    companion object {
        private const val TAG = "MosaicRectRenderer"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val TEXTURE_COORDS_VERTEX_NUMBER = 2
    }
}
