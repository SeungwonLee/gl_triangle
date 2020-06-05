package com.example.seungwon.openglestriangle.util.gles30

import android.opengl.GLES30
import android.util.Log

class GlErrorUtil {
    companion object {
        private const val TAG = "GlErrorUtil"

        fun checkGLError() {
            val error = GLES30.glGetError()
            var msg = when (error) {
                GLES30.GL_INVALID_ENUM -> "INVALID_ENUM"
                GLES30.GL_INVALID_VALUE -> "INVALID_VALUE"
                GLES30.GL_INVALID_OPERATION -> "INVALID_OPERATION"
                GLES30.GL_OUT_OF_MEMORY -> "OUT_OF_MEMORY"
                GLES30.GL_INVALID_FRAMEBUFFER_OPERATION -> "INVALID_FRAMEBUFFER_OPERATION"
                else -> "ERROR_NOT_FOUND"
            }
            if (error != GLES30.GL_NO_ERROR) {
                Log.e(TAG, "glError=${msg}")
                throw Exception("glError = ${error}, errorMsg = $msg")
            }
        }
    }
}