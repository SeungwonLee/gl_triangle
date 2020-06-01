package com.example.seungwon.openglestriangle.gles30.triangle

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.seungwon.openglestriangle.ProgramInfo
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
    private val vertexBuffer: FloatBuffer
    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX

    private var programHandle: Int = 0
    private var positionHandle: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
            positionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT,
            false, 0, vertexBuffer
        )

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(positionHandle)

        // Draw a triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

        // Destroy to use the triangle coordinate data
        GLES30.glDisableVertexAttribArray(positionHandle)
    }

    init {
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        if (gl == null) {
            return
        }

        Log.d("GL", "GL_RENDERER = " + gl.glGetString(GL10.GL_RENDERER))
        Log.d("GL", "GL_VENDOR = " + gl.glGetString(GL10.GL_VENDOR))
        Log.d("GL", "GL_VERSION = " + gl.glGetString(GL10.GL_VERSION))
        Log.i("GL", "GL_EXTENSIONS = " + gl.glGetString(GL10.GL_EXTENSIONS))

        val vertexCodeString =
            TextResourceReader.readTextFileFromAsset(
                context,
                "shaders/gl_es_30/triangle.vert"
            )
        val vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER)
        GLES30.glShaderSource(vertexShader, vertexCodeString)
        GLES30.glCompileShader(vertexShader)

        ShaderStatusInfo.getShaderStatus(vertexShader)

        val fragmentCodeString =
            TextResourceReader.readTextFileFromAsset(
                context,
                "shaders/gl_es_30/triangle.frag"
            )
        val fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER)
        GLES30.glShaderSource(fragmentShader, fragmentCodeString)
        GLES30.glCompileShader(fragmentShader)

        ShaderStatusInfo.getShaderStatus(fragmentShader)

        // Link verticesShader, fragmentShader to OpenGL
        programHandle = GLES30.glCreateProgram()

        if (programHandle == 0) {
            Log.w(TAG, "Could not create new program")
        }

        GLES30.glAttachShader(programHandle, vertexShader)
        GLES30.glAttachShader(programHandle, fragmentShader)

        GLES30.glLinkProgram(programHandle)

        ProgramInfo.validateProgram(programHandle)

        GLES30.glUseProgram(programHandle)

        // Get a handle of a position from the program
        positionHandle = GLES30.glGetAttribLocation(programHandle, "vPosition")
    }

    companion object {
        private const val TAG = "TriangleRenderer"
        private const val COORDS_PER_VERTEX = 3 // x, y, z
    }
}