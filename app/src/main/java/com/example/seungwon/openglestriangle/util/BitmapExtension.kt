package com.example.seungwon.openglestriangle.util

import android.graphics.Bitmap
import android.opengl.GLES20
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal fun Bitmap.saveToFile(filePath: String) {
    val file = File(filePath)
    val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
    compress(Bitmap.CompressFormat.JPEG, 100, os)
    os.close()
}

internal fun Bitmap.renderToBitmap(width: Int, height: Int) {
    val frameBufferWidth: Int = width
    val frameBufferHeight: Int = height
    val buffer =
        ByteBuffer.allocate(frameBufferWidth * frameBufferHeight * 4)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    GLES20.glReadPixels(
        0,
        0,
        frameBufferWidth,
        frameBufferHeight,
        GLES20.GL_RGBA,
        GLES20.GL_UNSIGNED_BYTE,
        buffer
    )
    buffer.rewind()
    // flip
    val tempByte = ByteArray(frameBufferWidth * 4)
    for (i in 0 until frameBufferHeight / 2) {
        buffer[tempByte]
        System.arraycopy(
            buffer.array(),
            buffer.limit() - buffer.position(),
            buffer.array(),
            buffer.position() - frameBufferWidth * 4,
            frameBufferWidth * 4
        )
        System.arraycopy(
            tempByte,
            0,
            buffer.array(),
            buffer.limit() - buffer.position(),
            frameBufferWidth * 4
        )
    }
    buffer.rewind()
    copyPixelsFromBuffer(buffer)
}
