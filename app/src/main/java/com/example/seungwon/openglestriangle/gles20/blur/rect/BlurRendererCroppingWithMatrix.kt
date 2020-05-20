package com.example.seungwon.openglestriangle.gles20.blur.rect

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.seungwon.openglestriangle.ProgramInfo
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.TextResourceReader
import com.example.seungwon.openglestriangle.util.FrameBufferUtil
import com.example.seungwon.openglestriangle.util.TxtLoaderUtil
import com.example.seungwon.openglestriangle.util.getTempFilePath
import com.example.seungwon.openglestriangle.util.renderToBitmap
import com.example.seungwon.openglestriangle.util.saveToFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BlurRendererCroppingWithMatrix(private val context: Context) : GLSurfaceView.Renderer {
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

    private val isDebug: Boolean = true

    private val halfVertexBuffer: FloatBuffer
    private val vertexBuffer: FloatBuffer
    private val txtBuffer: FloatBuffer
    private val txtBufferYFlip: FloatBuffer

    private val identityMatrixView: FloatArray = FloatArray(16)
    private val scaledTxtMatrixView: FloatArray = FloatArray(16)
    private val scaledVertexMatrix: FloatArray = FloatArray(16)
    private val projectionMatrix: FloatArray = FloatArray(16)
    private val scaledBlurAttachedViewVertexMatrix: FloatArray = FloatArray(16)
    private var scaledVertexMatrixForCrop: FloatArray = FloatArray(16)

    private var originalRenderingProgram: Int = 0
    private var gaussianBlurProgram: Int = -1

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
    private var isVerticalHandle: Int = 0

    private var width: Int = 0
    private var height: Int = 0

    private var textureFrameBufferA: FrameBufferUtil.TextureFrameBuffer? = null
    private var textureFrameBufferB: FrameBufferUtil.TextureFrameBuffer? = null
    private var cropFrameBuffer: FrameBufferUtil.TextureFrameBuffer? = null

    var onSurfaceSizeChanged: ((rect: Rect) -> Unit)? = null

    var blurOffset: Float = 1f
    var loopCount = 3

    @Volatile
    var textureRectStartPointX: Float = 0f

    @Volatile
    var textureRectStartPointY: Float = 0f

    @Volatile
    var vertexGlRectStartPointX: Float = 0f

    @Volatile
    var vertexGlRectStartPointY: Float = 0f

    @Volatile
    var textureRectEndPointX: Float = 0f

    @Volatile
    var textureRectEndPointY: Float = 0f

    @Volatile
    var vertexGlRectEndPointX: Float = 0f

    @Volatile
    var vertexGlRectEndPointY: Float = 0f

    @Volatile
    var scaledOriginalRectW = 0

    @Volatile
    var scaledOriginalRectH = 0

    var translationXGl: Float = 0f
    var translationYGl: Float = 0f

    var translationXOriginal: Float = 0f
    var translationYOriginal: Float = 0f

    var sizeOfViewRectW: Float = 0f
    var sizeOfViewRectH: Float = 0f

    var blurType: BlurType = BlurType.Gaussian

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
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // load blur txt shaders
        loadBlurProgram()

        renderOriginalTexture(
            originalTextureId,
            0,
            halfVertexBuffer,
            txtBuffer,
            scaledVertexMatrix,
            identityMatrixView,
            projectionMatrix
        )

        // render scene to FBO A
        renderScene(
            vertexBuffer,
            txtBufferYFlip,
            textureFrameBufferA?.frameBufferId ?: 0,
            originalTextureId
        )

//        for (i in 0 until loopCount) {
////             render FBO A to FBO B, using horizontal blur
//            renderHorizontalBlur(vertexBuffer, txtBufferYFlip)
//
//            // render FBO B to scene, using vertical blur
//            renderVerticalBlur(vertexBuffer, txtBufferYFlip)
//        }

        renderCrop(halfVertexBuffer, txtBufferYFlip, textureFrameBufferA?.textureId ?: 0)
//            renderCropWithRect(rectVertexBuffer, rectTxtBuffer, textureFrameBufferA?.textureId ?: 0)

        renderScene2(
            halfVertexBuffer,
            txtBuffer,
            cropFrameBuffer?.textureId ?: 0
        )
    }

    private fun renderOriginalTexture(
        originalTextureId: Int,
        frameBufferId: Int,
        vertexBuffer: FloatBuffer,
        textureBuffer: FloatBuffer,
        scaledVertexMatrix: FloatArray,
        scaledTxtMatrixView: FloatArray,
        projectionMatrix: FloatArray
    ) {
        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, scaledVertexMatrix, 0)
        GLES20.glUniformMatrix4fv(originalTextureMvpHandle, 1, false, scaledTxtMatrixView, 0)
        GLES20.glUniformMatrix4fv(originalProjectionMatrixHandle, 1, false, projectionMatrix, 0)
        GLES20.glEnableVertexAttribArray(originalPositionHandle)
        GLES20.glVertexAttribPointer(
            originalPositionHandle,
            X_Y_COORDS_NUMBER, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(originalTextureCoordsHandle)
        GLES20.glVertexAttribPointer(
            originalTextureCoordsHandle,
            TEXTURE_COORDS_VERTEX_NUMBER, GLES20.GL_FLOAT, false, 0, textureBuffer
        )

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, originalTextureId)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(originalPositionHandle)
        GLES20.glDisableVertexAttribArray(originalTextureCoordsHandle)
    }

    private fun renderVerticalBlur(vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        val textureFrameBufferA = textureFrameBufferA ?: error("textureFrameBufferA null")
        val textureFrameBufferB = textureFrameBufferB ?: error("textureFrameBufferB null")

        GLES20.glUseProgram(gaussianBlurProgram)

        textureFrameBufferA.bind()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBufferB.textureId)

        GLES20.glUniform1f(texelHeightOffset, 0f)
        GLES20.glUniform1f(texelWidthOffset, blurOffset / (width / 3f))
//        GLES20.glUniform1f(isVerticalHandle, 1f)

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, identityMatrixView, 0)
        GLES20.glUniformMatrix4fv(textureMvpHandle, 1, false, identityMatrixView, 0)
        GLES20.glUniformMatrix4fv(blurProjectionMatrixHandle, 1, false, identityMatrixView, 0)

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

//        saveToFile("_v")

        textureFrameBufferA.unbind()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordsHandle)
    }

    private fun renderHorizontalBlur(vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        val textureFrameBufferA = textureFrameBufferA ?: error("textureFrameBufferA null")
        val textureFrameBufferB = textureFrameBufferB ?: error("textureFrameBufferB null")
//        Matrix.setIdentityM(matrixView, 0)

        GLES20.glUseProgram(gaussianBlurProgram)
        textureFrameBufferB.bind()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBufferA.textureId)

        GLES20.glUniform1f(texelHeightOffset, blurOffset / (height / 3f))
        GLES20.glUniform1f(texelWidthOffset, 0f)
//        GLES20.glUniform1f(isVerticalHandle, 0f)

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, identityMatrixView, 0)
        GLES20.glUniformMatrix4fv(textureMvpHandle, 1, false, identityMatrixView, 0)
        GLES20.glUniformMatrix4fv(blurProjectionMatrixHandle, 1, false, identityMatrixView, 0)

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

        textureFrameBufferB.unbind()
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

        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, identityMatrixView, 0)
        GLES20.glUniformMatrix4fv(originalTextureMvpHandle, 1, false, identityMatrixView, 0)
        GLES20.glUniformMatrix4fv(originalProjectionMatrixHandle, 1, false, identityMatrixView, 0)

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

//        saveToFile("_o", width, height)
//        saveToFile("_o2", lengthOfFboW, lengthOfFboH)

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
        textureId: Int
    ) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glViewport(0, 0, width, height)

        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniformMatrix4fv(
            originalMvpHandle,
            1,
            false,
            scaledBlurAttachedViewVertexMatrix,
            0
        )
        GLES20.glUniformMatrix4fv(originalTextureMvpHandle, 1, false, identityMatrixView, 0)
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

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(originalPositionHandle)
        GLES20.glDisableVertexAttribArray(originalTextureCoordsHandle)
    }

    // not matched [-1, 1] coords of the original in rect fbo
    private fun renderCrop(
        vertexBuffer: FloatBuffer,
        textureBuffer: FloatBuffer,
        textureId: Int
    ) {
        val projectionMatrix = FloatArray(16)
        Matrix.setIdentityM(projectionMatrix, 0)
        cropFrameBuffer?.let {
            Matrix.orthoM(
                projectionMatrix,
                0,
                it.width * -0.5f,
                it.width * 0.5f,
                it.height * -0.5f,
                it.height * 0.5f,
                -1f,
                1f
            )

            // TODO no set View port
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, it.frameBufferId)
            GLES20.glViewport(0, 0, it.width, it.height)
        }

        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, scaledVertexMatrixForCrop, 0)
        GLES20.glUniformMatrix4fv(originalTextureMvpHandle, 1, false, identityMatrixView, 0)
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

        saveToFile("_crop")
        saveToFile("_crop2", scaledOriginalRectW, scaledOriginalRectH)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(originalPositionHandle)
        GLES20.glDisableVertexAttribArray(originalTextureCoordsHandle)
    }

    private fun renderCropWithRect(
        vertexBuffer: FloatBuffer,
        textureBuffer: FloatBuffer,
        textureId: Int
    ) {
//        val projectionMatrix = FloatArray(16)
        cropFrameBuffer?.let {
//            Matrix.orthoM(
//                projectionMatrix,
//                0,
//                it.width * -0.5f,
//                it.width * 0.5f,
//                it.height * -0.5f,
//                it.height * 0.5f,
//                -1f,
//                1f
//            )

//            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, it.frameBufferId)
            it.bind()
        }

        GLES20.glUseProgram(originalRenderingProgram)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniformMatrix4fv(originalMvpHandle, 1, false, scaledVertexMatrix, 0)
        GLES20.glUniformMatrix4fv(originalTextureMvpHandle, 1, false, identityMatrixView, 0)
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

        saveToFile("_crop")
        saveToFile("_crop2", scaledOriginalRectW, scaledOriginalRectH)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisableVertexAttribArray(originalPositionHandle)
        GLES20.glDisableVertexAttribArray(originalTextureCoordsHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height

        onSurfaceSizeChanged?.invoke(Rect(0, 0, width, height))

//        val frameBufferWidth = 500
//        val frameBufferHeight = 500

        Log.d(TAG, "onSurfaceChanged $width $height")

        GLES20.glViewport(0, 0, width, height)

        Matrix.setIdentityM(scaledBlurAttachedViewVertexMatrix, 0)
        Matrix.setIdentityM(identityMatrixView, 0)
        Matrix.setIdentityM(scaledVertexMatrix, 0)
        Matrix.setIdentityM(scaledTxtMatrixView, 0)
        Matrix.setIdentityM(projectionMatrix, 0)

//        val temp2 = FloatArray(16)
//        Matrix.setIdentityM(temp2, 0)
//        Matrix.scaleM(temp2, 0, width.toFloat(), height.toFloat(), 0f)
        val translationMatrix = FloatArray(16)
        Matrix.setIdentityM(translationMatrix, 0)
//        Matrix.translateM(translationMatrix, 0, 470f, 200f, 0f)

        val scaledMatrixForOriginal = FloatArray(16)
        Matrix.setIdentityM(scaledMatrixForOriginal, 0)
        Matrix.scaleM(
            scaledMatrixForOriginal,
            0, /*width.toFloat()*/
            BlurScaledActivity.SCALED_WIDTH, /*height.toFloat()*/
            BlurScaledActivity.SCALED_HEIGHT,
            0f
        )
        Matrix.multiplyMM(scaledVertexMatrix, 0, translationMatrix, 0, scaledMatrixForOriginal, 0)

        Matrix.orthoM(
            projectionMatrix,
            0,
            width * -0.5f,
            width * 0.5f,
            height * -0.5f,
            height * 0.5f,
            -1f,
            1f
        )

        textureFrameBufferA = FrameBufferUtil.createFrameTextureBuffer(
            width,
            height
        )
        textureFrameBufferB = FrameBufferUtil.createFrameTextureBuffer(
            width,
            height
        )
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // load original txt shaders
        originalRenderingProgram = ProgramInfo.createProgram(
            context,
            R.raw.simple_txt_vertex_shader_with_matrix,
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
        originalTextureMvpHandle =
            GLES20.glGetUniformLocation(originalRenderingProgram, "uTextureMatrix")
        originalProjectionMatrixHandle =
            GLES20.glGetUniformLocation(originalRenderingProgram, "uProjectionMatrix")
    }

    private fun loadBlurProgram() {
        if (gaussianBlurProgram != -1) {
            GLES20.glDeleteProgram(gaussianBlurProgram)
        }

        val vertexShader = when (blurType) {
            BlurType.StackBlur, BlurType.Box -> "shaders/stack_blur_hoko.vert"
            BlurType.Gaussian -> "shaders/blur_pass_through_vertex_shader.glsl"
            BlurType.Gaussian_2 -> "shaders/blur_pass_through_vertex_shader.glsl"
        }
        val fragmentShader = when (blurType) {
            BlurType.Box -> "shaders/box_blur.frag"
            BlurType.StackBlur -> "shaders/stack_blur_hoko.frag"
            BlurType.Gaussian -> "shaders/blur_pass_through_fragment_shader.glsl"
            BlurType.Gaussian_2 -> "shaders/gaussian_vertical.frag"
        }
        val vertexShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            vertexShader
        )
        val fragmentShaderStr = TextResourceReader.readTextFileFromAsset(
            context,
            fragmentShader
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
        blurProjectionMatrixHandle =
            GLES20.glGetUniformLocation(gaussianBlurProgram, "uProjectionMatrix")

        texelWidthOffset = GLES20.glGetUniformLocation(gaussianBlurProgram, "uTexelWidthOffset")
        texelHeightOffset =
            GLES20.glGetUniformLocation(gaussianBlurProgram, "uTexelHeightOffset")

//        isVerticalHandle = GLES20.glGetUniformLocation(gaussianBlurProgram, "isVertical")

        if (blurType == BlurType.StackBlur || blurType == BlurType.Box) {
            val blurSizeHandle = GLES20.glGetUniformLocation(gaussianBlurProgram, "uBlurSize")
            GLES20.glUniform1i(blurSizeHandle, BLUR_RATIO)
        }
//        GLES20.glUniform1f(texelHeightOffset, BLUR_RATIO / /*bitmapWidth*/700f)
//        GLES20.glUniform1f(
//            texelWidthOffset,
//            BLUR_RATIO / /*bitmapWidth*/700f
//        )
    }

    fun onDrawBlurRect(postAction: () -> Unit): Boolean {
        Log.d(TAG, "onTouch")
        if (width == 0 || height == 0) {
            Log.e(TAG, "onTouch $width $height == 0")
            return false
        }

        createCroppedTextureWithMatrix()
        postAction.invoke()

        return true
    }

    private fun createCroppedTextureWithMatrix() {
        cropFrameBuffer =
            FrameBufferUtil.createFrameTextureBuffer(scaledOriginalRectW, scaledOriginalRectH)

        // transform for crop
        val scaledMatrix = FloatArray(16)
        Matrix.setIdentityM(scaledMatrix, 0)
        Matrix.scaleM(
            scaledMatrix,
            0,
            BlurScaledActivity.SCALED_WIDTH
            /*scaledOriginalRectW.toFloat() * width / scaledOriginalRectW.toFloat()*/
            /*width.toFloat()*/,
            BlurScaledActivity.SCALED_HEIGHT
            /*scaledOriginalRectH.toFloat() * height / scaledOriginalRectH.toFloat()*/
            /*height.toFloat()*/,
            0f
        )

        val translateMatrix = FloatArray(16)
        Matrix.setIdentityM(translateMatrix, 0)
        val centerX = (width.toFloat() * 0.5f - scaledOriginalRectW * 0.5f)
        val centerY = (height.toFloat() * 0.5f - scaledOriginalRectH * 0.5f)
        Matrix.translateM(
            translateMatrix,
            0, /*translationXGl * -1*/
            centerX - translationXGl, /*translationYGl * -1*/
            centerY - translationYGl /*translationYGl*/,
            0f
        )

        scaledVertexMatrixForCrop = FloatArray(16)
        Matrix.setIdentityM(scaledVertexMatrixForCrop, 0)
        Matrix.multiplyMM(scaledVertexMatrixForCrop, 0, translateMatrix, 0, scaledMatrix, 0)

        // transform for rendering on a view
        val translateMatrixForView = FloatArray(16)
        Matrix.setIdentityM(translateMatrixForView, 0)
        Matrix.translateM(
            translateMatrixForView,
            0,
            /*(-width * 0.5f) + (sizeOfViewRectW * 0.5f) + translationXOriginal*/
            (-width * 0.5f) + (sizeOfViewRectW * 0.5f) + translationXOriginal,
            /*(height * 0.5f) - (sizeOfViewRectH * 0.5f) - translationYOriginal*/
            (height * 0.5f) - (sizeOfViewRectH * 0.5f) - translationYOriginal,
            0f
        )

        Log.d(TAG, "translationXGl $translationXGl $translationYGl")
        Log.d(TAG, "lengthOfFboW $scaledOriginalRectW $scaledOriginalRectH")
        Log.d(TAG, "lengthOfOriginalW $sizeOfViewRectW $sizeOfViewRectH")

        val scaledMatrixForView = FloatArray(16)
        Matrix.setIdentityM(scaledMatrixForView, 0)
        Matrix.scaleM(
            scaledMatrixForView,
            0,
            sizeOfViewRectW,
            sizeOfViewRectH,
            0f
        )

        Matrix.multiplyMM(
            scaledBlurAttachedViewVertexMatrix,
            0,
            translateMatrixForView,
            0,
            scaledMatrixForView,
            0
        )
    }

    private fun saveToFile(
        postFileName: String = "",
        width: Int = this.width,
        height: Int = this.height
    ) {
        if (isDebug && width > 0 && height > 0) {
            val filePath = getTempFilePath(context.externalCacheDir.absolutePath, postFileName)
            val bitmap =
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.renderToBitmap(width, height)
            bitmap.saveToFile(filePath)
            bitmap.recycle()
        }
    }

    companion object {
        private const val TAG = "BlurRectRenderer"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val TEXTURE_COORDS_VERTEX_NUMBER = 2

        private const val BLUR_RATIO = 2
        private const val BLUR_OFFSET = 1.3846153846f//0.004f//0.003f//0.00001f
        private const val WIDTH_DIVIDER = 3f
        //0.003f//0.002f//1.3846153846f//1.3846153846f//0.003155048076953f'
    }
}
