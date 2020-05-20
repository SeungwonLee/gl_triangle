package com.example.seungwon.openglestriangle.gles20.translation

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.ShaderStatusInfo
import com.example.seungwon.openglestriangle.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SquareRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val squareCoords: FloatArray = floatArrayOf(
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f // top right
    )
    private val colors: FloatArray = floatArrayOf(
            1.0f, 0.0f, 0f, 1.0f
    )
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    private val vertexCount: Int = squareCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    private val vertexBuffer: FloatBuffer
    private val drawOrderBuffer: ShortBuffer

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0

    init {
        vertexBuffer = ByteBuffer.allocateDirect(squareCoords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)

        drawOrderBuffer = ByteBuffer
                .allocateDirect(drawOrder.size * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
        drawOrderBuffer.put(drawOrder)
        drawOrderBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Prepare the square coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, vertexBuffer)

        // Set a color for drawing the square
        GLES20.glUniform4fv(colorHandle, 1, colors, 0)

        // Draw a square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size,
                GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer)

        // Destroy to use the square coordinate data
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        val vertextCodeString = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader)
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertextCodeString)
        GLES20.glCompileShader(vertexShader)

        ShaderStatusInfo.getShaderStatus(vertexShader)

        Log.d(TAG, "vertextCodeString $vertextCodeString")

        val fragmentCodeString = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader)
        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentCodeString)
        GLES20.glCompileShader(fragmentShader)

        ShaderStatusInfo.getShaderStatus(fragmentShader)

        Log.d(TAG, "fragmentCodeString $fragmentCodeString")

        // Link verticesShader, fragmentShader to OpenGL
        program = GLES20.glCreateProgram()

        if (program == 0) {
            Log.w(TAG, "Could not create new program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)

        GLES20.glLinkProgram(program)
        GLES20.glUseProgram(program)

        // Get a handle of a position from the program
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        // Get a handle of a color from fragment shader's
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        Log.d(TAG, "positionHandle $positionHandle colorHandle $colorHandle")
    }

    companion object {
        private const val TAG = "SquareRenderer"
        private const val COORDS_PER_VERTEX = 3
    }
}