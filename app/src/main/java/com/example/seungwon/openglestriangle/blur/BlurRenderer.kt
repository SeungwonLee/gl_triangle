package com.example.seungwon.openglestriangle.blur

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
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
        // X, Y
        1f, 1f, -1f, 1f, -1f, -1f,
        -1f, -1f, 1f, -1f, 1f, 1f
    )
    private val vertexBuffer: FloatBuffer

    private var passThroughProgram: Int = 0
    private var guassianVerticalProgram: Int = 0
    private var guassianHorizontalProgram: Int = 0

    private var bogumTextureId: Int = 0

    private val textureFrameBuffers: ArrayList<FrameBufferUtil.TextureFrameBuffer> = ArrayList()

    init {
        vertexBuffer =
            ByteBuffer.allocateDirect(squareCoords.size * FLOAT_BYTE_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(squareCoords)
        vertexBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        //render scene
//        GLES20.glUseProgram(passThroughProgram)
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, SQUARE_LINE_COUNT)

        //render scene to FBO A, using horizontal blur
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBuffers[0].frameBufferId) // TODO 30
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bogumTextureId)
        GLES20.glUseProgram(guassianHorizontalProgram)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, SQUARE_LINE_COUNT)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0) // TODO 30

        //render FBO B to scene, using vertical blur
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textureFrameBuffers[1].frameBufferId) // TODO 30
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[0].textureId)
        GLES20.glUseProgram(guassianVerticalProgram)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, SQUARE_LINE_COUNT)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0) // TODO 30

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0) // TODO 30
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureFrameBuffers[0].textureId)
        GLES20.glUseProgram(passThroughProgram)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, SQUARE_LINE_COUNT)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // load shaders
        passThroughProgram = ProgramInfo.createProgramV30(
            context,
            R.raw.blur_pass_through_vertex_shader,
            R.raw.blur_pass_through_fragment_shader
        )
        guassianVerticalProgram = ProgramInfo.createProgramV30(
            context,
            R.raw.blur_pass_through_vertex_shader,
            R.raw.blur_gaussian_linear_vertex_fragment_shader
        )
        guassianHorizontalProgram = ProgramInfo.createProgramV30(
            context,
            R.raw.blur_pass_through_vertex_shader,
            R.raw.blur_gaussian_linear_horiz_fragment_shader
        )

        // create temporary framebuffer
        textureFrameBuffers.add(FrameBufferUtil.createFrameTextureBuffer())
        textureFrameBuffers.add(FrameBufferUtil.createFrameTextureBuffer())

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0) // TODO 30

        // load a texture of the target image
        val bitmap = TxtLoaderUtil.getBitmap(context, R.drawable.bogum)
        bogumTextureId = TxtLoaderUtil.getTxt(bitmap)

        // create full-screen quad mesh
        val bufferIds = IntArray(1)
        GLES20.glGenBuffers(1, bufferIds, 0)
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            X_Y_COORDS_NUMBER * FLOAT_BYTE_SIZE * SQUARE_X_Y_COUNT,
            vertexBuffer,
            GLES20.GL_STATIC_DRAW
        )
        GLES20.glEnableVertexAttribArray(0)
        GLES20.glVertexAttribPointer(
            0,
            X_Y_COORDS_NUMBER,
            GLES20.GL_FLOAT,
            false,
            FLOAT_BYTE_SIZE * X_Y_COORDS_NUMBER,
            0
        )
    }

    companion object {
        private const val TAG = "BlurRenderer"

        private const val X_Y_COORDS_NUMBER = 2
        private const val FLOAT_BYTE_SIZE = 4
        private const val SQUARE_X_Y_COUNT = 6
        private const val SQUARE_LINE_COUNT = 6
    }
}
