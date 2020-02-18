package com.example.seungwon.openglestriangle.util

import android.opengl.GLES20
import android.opengl.GLES20.glCheckFramebufferStatus
import android.util.Log

object FrameBufferUtil {

    private const val TAG = "FrameBufferUtil"

    fun createFrameTextureBuffer(): TextureFrameBuffer {
        val textureIds = IntArray(2)
        GLES20.glGenTextures(1, textureIds, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGB,
            1024,
            1024,
            0,
            GLES20.GL_RGB,
            GLES20.GL_UNSIGNED_BYTE,
            null
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )

        // Generate frame buffer object.
        val frameBufferIds = IntArray(2)

        GLES20.glGenFramebuffers(1, frameBufferIds, 0)
        Log.d(TAG, "glGenFramebuffers ${frameBufferIds[0]}")
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferIds[0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            textureIds[0],
            0
        )
        when (glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)) {
            GLES20.GL_FRAMEBUFFER_COMPLETE ->
                return TextureFrameBuffer(
                    frameBufferIds[0], textureIds[0]
                )
            GLES20.GL_FRAMEBUFFER_UNSUPPORTED ->
                error("GL_FRAMEBUFFER_UNSUPPORTED")
            else ->
                error("GL_FRAMEBUFFER_UNSUPPORTED")
        }
    }

    data class TextureFrameBuffer(val frameBufferId: Int, val textureId: Int)
}