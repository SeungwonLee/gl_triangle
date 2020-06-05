package com.example.seungwon.openglestriangle.gles30.drawer

import android.opengl.GLES30
import com.example.seungwon.openglestriangle.model.Triangle

class TriangleDrawer {
    private val triangle: Triangle = Triangle()
    private var positionHandle: Int = 0

    fun init(program: Int) {
        // Get a handle of a position from the program
        positionHandle = GLES30.glGetAttribLocation(program, POSITION_HANDLE_NAME)
    }

    fun draw() {
        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
            positionHandle, Triangle.COORDS_PER_VERTEX, GLES30.GL_FLOAT,
            false, 0, triangle.vertexBuffer
        )

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(positionHandle)

        // Draw a triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, triangle.vertexCount)

        // Destroy to use the triangle coordinate data
        GLES30.glDisableVertexAttribArray(positionHandle)
    }

    companion object {
        private const val POSITION_HANDLE_NAME = "vPosition"
    }
}