package com.example.seungwon.openglestriangle.model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle {
    private val triangleCoords: FloatArray = floatArrayOf(
        0.0f, 0.5f, 0.0f, // top
        -0.5f, -0.5f, 0.0f, // bottom left
        0.5f, -0.5f, 0.0f // bottom right
    )
    val vertexBuffer: FloatBuffer
    val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    val vertexCoordsCount: Int = triangleCoords.size

    init {
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
    }

    companion object {
        const val COORDS_PER_VERTEX = 3 // x, y, z
    }
}