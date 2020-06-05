package com.example.seungwon.openglestriangle.model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Texture {
    private val txtCoords: FloatArray = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )
    private val txtBuffer: FloatBuffer

    init {
        txtBuffer = ByteBuffer.allocateDirect(txtCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        txtBuffer.put(txtCoords)
        txtBuffer.position(0)
    }

    fun getTextureBuffer(): FloatBuffer = txtBuffer

    companion object {
        const val TXT_COORDS_PER_VERTEX = 2
    }
}
