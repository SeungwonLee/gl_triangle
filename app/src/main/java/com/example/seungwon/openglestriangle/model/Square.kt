package com.example.seungwon.openglestriangle.model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Square(fullSize: Boolean = true) {
    private val squareCoords: FloatArray = floatArrayOf(
        // X, Y
        -1f, -1f,
        1f, -1f,
        -1f, 1f,
        1f, 1f
    )
    private val halfSquareCoords: FloatArray = floatArrayOf(
        // X, Y
        -0.5f, -0.5f,
        0.5f, -0.5f,
        -0.5f, 0.5f,
        0.5f, 0.5f
    )
    val vertexBuffer: FloatBuffer
    val vertexCount: Int
    val vertexCoordsCount: Int

    init {
        val array = if (fullSize) squareCoords else halfSquareCoords
        vertexBuffer = ByteBuffer.allocateDirect(array.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(array)
        vertexBuffer.position(0)

        vertexCount = array.size / COORDS_PER_VERTEX
        vertexCoordsCount = array.size
    }

    companion object {
        const val COORDS_PER_VERTEX = 2 // x, y
    }
}
