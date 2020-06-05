package com.example.seungwon.openglestriangle.gles30.drawer

import android.opengl.GLES30
import com.example.seungwon.openglestriangle.model.Square

class SquareDrawer {
    private val square: Square = Square()
    private var positionHandle: Int = 0

    fun init(program: Int) {
        positionHandle = GLES30.glGetAttribLocation(
            program,
            POSITION_HANDLE_NAME
        )
    }

    fun draw() {
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            Square.COORDS_PER_VERTEX, GLES30.GL_FLOAT, false,
            0, square.vertexBuffer
        )
        GLES30.glDrawArrays(
            GLES30.GL_TRIANGLE_STRIP,
            0,
            square.vertexCount
        )
        GLES30.glDisableVertexAttribArray(positionHandle)
    }

    companion object {
        private const val FLOAT_BYTE_SIZE = 4
        private const val POSITION_HANDLE_NAME = "vPosition"
    }
}
