package com.example.seungwon.openglestriangle.gles30.drawer

import android.opengl.GLES30
import com.example.seungwon.openglestriangle.model.Triangle
import com.example.seungwon.openglestriangle.util.GlUtils

class TriangleDrawerVao : Drawer {
    private val triangle: Triangle = Triangle()

    private var positionHandle: Int = 0
    private var positionVboHandle: Int = 0
    private var positionVaoHandle: Int = 0

    override fun init(program: Int) {
        positionHandle = GLES30.glGetAttribLocation(program, POSITION_HANDLE_NAME)

        // init vbo
        val vboIds = IntArray(1)
        GLES30.glGenBuffers(1, vboIds, 0)
        positionVboHandle = vboIds[0]
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, positionVboHandle)
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            triangle.vertexCoordsCount * GlUtils.FLOAT_SIZE,
            triangle.vertexBuffer,
            GLES30.GL_STATIC_DRAW
        )

        // Init vao
        val vaoIds = IntArray(1)
        GLES30.glGenVertexArrays(1, vaoIds, 0)
        positionVaoHandle = vaoIds[0]
        GLES30.glBindVertexArray(positionVaoHandle)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, positionVboHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            Triangle.COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            0,
            0
        )
        GLES30.glEnableVertexAttribArray(positionHandle)

        GLES30.glBindVertexArray(0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glDisableVertexAttribArray(positionHandle)
    }

    override fun draw(program: Int) {
        // bind the vao
        GLES30.glBindVertexArray(positionVaoHandle)

        // draw a triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, triangle.vertexCount)

        // return to the default VAO, VBO, attribute
        GLES30.glBindVertexArray(0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glDisableVertexAttribArray(positionVaoHandle)
    }

    override fun release() {
        GLES30.glDeleteBuffers(1, intArrayOf(positionVboHandle), 0)
        GLES30.glDeleteVertexArrays(1, intArrayOf(positionVaoHandle), 0)
    }

    companion object {
        private const val POSITION_HANDLE_NAME = "vPosition"
    }
}
