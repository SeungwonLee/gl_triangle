package com.example.seungwon.openglestriangle.triangle

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.seungwon.openglestriangle.ProgramInfo
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.ShaderStatusInfo
import com.example.seungwon.openglestriangle.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TriangleRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val triangleCoords: FloatArray = floatArrayOf(
            0.0f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f // bottom right
    )
    private val colors: FloatArray = floatArrayOf(
            0.0f, 1.0f, 0f, 1.0f // R,G,B,A
    )

    // 9 (x,y,z)*3 / 3 = 3
    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    // 3 (x,y,z) * 4 byte = 12
    private val vertexStride: Int = COORDS_PER_VERTEX * 4
    private val vertexBuffer: FloatBuffer
    private var program: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0

    private var width = 0
    private var height = 0

    init {
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Set a color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, colors, 0)

        // Draw a triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        // Destroy to use the triangle coordinate data
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height

        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        val vertexCodeString = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader)
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertexCodeString)
        GLES20.glCompileShader(vertexShader)

        ShaderStatusInfo.getShaderStatus(vertexShader)

        val fragmentCodeString = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader)
        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentCodeString)
        GLES20.glCompileShader(fragmentShader)

        Log.d("onSurfaceCreated", "fragmentCodeString $fragmentCodeString")
        ShaderStatusInfo.getShaderStatus(fragmentShader)

        // Link verticesShader, fragmentShader to OpenGL
        program = GLES20.glCreateProgram()

        if (program == 0) {
            Log.w(TAG, "Could not create new program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)

        GLES20.glLinkProgram(program)

        ProgramInfo.validateProgram(program)

        GLES20.glUseProgram(program)

        // Get a handle of a position from the program
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        // Get a handle of a color from fragment shader's
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")

        // Prepare the triangle coordinate data
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, vertexBuffer)
    }

    companion object {
        private const val COORDS_PER_VERTEX = 3 // x, y, z
        private const val TAG = "TriangleRenderer"
    }
}
