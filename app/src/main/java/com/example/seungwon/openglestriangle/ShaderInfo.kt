package com.example.seungwon.openglestriangle

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.util.Log


object ShaderInfo {

    private const val TAG = "ShaderInfo"

    fun getShaderStatus(shaderObjectId: Int): Int {
        // Get the compilation status.
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

        // Print the shader info log to the Android log output.
        Log.v(TAG, "Results of compiling source:" + "\n"
                + glGetShaderInfoLog(shaderObjectId))

        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId)

            Log.w(TAG, "Compilation of shader failed.")

            return 0
        }

        Log.v(TAG, "Compilation of shader compiled.")
        return shaderObjectId
    }
}
