package com.example.seungwon.openglestriangle

import android.opengl.GLES20
import android.util.Log


object ShaderStatusInfo {

    private const val TAG = "ShaderStatusInfo"

    fun getShaderStatus(shaderObjectId: Int): Int {
        // Get the compilation status.
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        // Print the shader info log to the Android log output.
        Log.v(
            TAG, "Results of compiling source:" + "\n"
                    + GLES20.glGetShaderInfoLog(shaderObjectId)
        )

        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            GLES20.glDeleteShader(shaderObjectId)
            Log.w(TAG, "Compilation of shader failed.")
            return 0
        }

        Log.v(TAG, "Compilation of shader compiled.")
        return shaderObjectId
    }
}
