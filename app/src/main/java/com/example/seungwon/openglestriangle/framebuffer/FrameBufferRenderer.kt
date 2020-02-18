package com.example.seungwon.openglestriangle.framebuffer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.seungwon.openglestriangle.ProgramInfo
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.util.TxtLoaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class FrameBufferRenderer(val context: Context) : GLSurfaceView.Renderer {
    private var isFirst: Boolean = true
    private var width: Int = 0
    private var height: Int = 0

    private val squareCoords: FloatArray = floatArrayOf(
        // X, Y
        -1f, -1f,
        1f, -1f,
        1f, 1f,
        -1f, 1f
    )
    private val catCoords: FloatArray = floatArrayOf(
        // X, Y
        0.3f, 0.3f,
        0.7f, 0.3f,
        0.7f, 0.7f,
        0.3f, 0.7f
    )
    private val txtCoords: FloatArray = floatArrayOf(
        // U, V
        0f, 1f,
        1f, 1f,
        1f, 0f,
        0f, 0f
    )
    private val matrixView = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private val vertexBuffer: FloatBuffer
    private val catVertexBuffer: FloatBuffer
    private val txtBuffer: FloatBuffer

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var txtCoordHandle: Int = 0
    private var matrixHandle: Int = 0
    private var bitmapHandle: Int = 0
    private var catBitmapHandle: Int = 0

    private var frameBufferHandle: Int = 0
    private var catFrameBufferHandle: Int = 0

    private var angleOffset = 0

    init {
        vertexBuffer = ByteBuffer.allocateDirect(squareCoords.size * FLOAT_SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(squareCoords)
        vertexBuffer.position(0)

        txtBuffer = ByteBuffer.allocateDirect(txtCoords.size * FLOAT_SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(txtCoords)
        txtBuffer.position(0)

        catVertexBuffer = ByteBuffer.allocateDirect(catCoords.size * FLOAT_SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(catCoords)
        catVertexBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

        // OpenGL that future texture calls should be applied to this texture object
        // draw bogum


        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )

        txtCoordHandle = GLES20.glGetAttribLocation(program, "a_texCoord")
        GLES20.glEnableVertexAttribArray(txtCoordHandle)
        GLES20.glVertexAttribPointer(
            txtCoordHandle,
            TXT_COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            0,
            txtBuffer
        )

        // matrixHandle will be used in orthoM for projection.
        matrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, projectionMatrix, 0)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapHandle)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        // rotate cat
//        val copyProjectionMatrix = FloatArray(16)
//        val matrixViewMatrix = FloatArray(16)
//
//        Matrix.setIdentityM(matrixViewMatrix, 0)
//
//        System.arraycopy(projectionMatrix, 0, copyProjectionMatrix, 0, copyProjectionMatrix.size)
//
//        Matrix.rotateM(matrixViewMatrix, 0, angleOffset % 360f, 0f, 1f, -1f)

        // copy to projection
//        val temp = FloatArray(16)
//        Matrix.multiplyMM(temp, 0, copyProjectionMatrix, 0, matrixViewMatrix, 0)
//        System.arraycopy(temp, 0, copyProjectionMatrix, 0, temp.size)

        // draw cat
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, catFrameBufferHandle)

//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, catBitmapHandle)
//
//        // Attach Texture to FBO.
//        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, catBitmapHandle, 0)
//
//        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, catVertexBuffer)
//
//        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, copyProjectionMatrix, 0)
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        if (isFirst) {
            TxtLoaderUtil.saveFrame(bitmapHandle, width, height)
            isFirst = false
        }

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(txtCoordHandle)

//        angleOffset += 1
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height

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
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
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

        program = ProgramInfo.createProgram(
            context,
            R.raw.simple_txt_vertex_shader,
            R.raw.simple_txt_fragment_shader
        )
        GLES20.glUseProgram(program)

        // Generate the frame buffer object.
        frameBufferHandle = initFrameBuffer()

        // Generate the frame buffer object.
//        catFrameBufferHandle = initFrameBuffer()
//        attachFrameBuffer(catBitmapHandle)
//        attachFrameBuffer(catBitmapHandle)

        val bitmap = TxtLoaderUtil.getBitmap(context, R.drawable.bogum)
        bitmapHandle = TxtLoaderUtil.getTxt(bitmap)

        attachFrameBuffer(bitmapHandle)
//        val catBitmap = TxtLoaderUtil.getBitmap(context, R.drawable.cuty_cat_3)
//        catBitmapHandle = TxtLoaderUtil.getTxt(catBitmap)

        bitmap.recycle()
//        catBitmap.recycle()

    }

    private fun attachFrameBuffer(txtHandleId: Int): Int {
        // attach color buffers to this FBO.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle)

        // Attach Texture to FBO.
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            txtHandleId,
            0
        )

        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
        Log.e(TAG, "status=${(status == GLES20.GL_FRAMEBUFFER_COMPLETE)}")

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        return status
    }

    private fun initFrameBuffer(): Int {
        val frameBufferIds = IntArray(2)

        // Generate frame buffer object.
        GLES20.glGenFramebuffers(1, frameBufferIds, 0)

        Log.e(TAG, "glGenFramebuffers ${frameBufferIds[0]}")

        return frameBufferIds[0]
    }

    companion object {
        private const val TAG = "FrameBufferRenderer"
        private const val COORDS_PER_VERTEX = 2
        private const val TXT_COORDS_PER_VERTEX = 2
        private const val FLOAT_SIZE_BYTES = 4
        private const val TRIANGLE_VERTICES_DATA_STRIDE_BYTES =
            (COORDS_PER_VERTEX + TXT_COORDS_PER_VERTEX) * FLOAT_SIZE_BYTES
    }
}
