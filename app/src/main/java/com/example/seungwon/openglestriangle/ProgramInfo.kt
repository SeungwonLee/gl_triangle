package com.example.seungwon.openglestriangle

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.util.Log


object ProgramInfo {
    /**
     * Validates an OpenGL program. Should only be called when developing the
     * application.
     */
    fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)

        val validateStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
        Log.v("ProgramInfo", "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjectId))

        val result = validateStatus[0] == GLES20.GL_FALSE

        if (result) {
            Log.v("ProgramInfo", "Results of validating Fails")
        }

        Log.v("ProgramInfo", "Results of validating Success")
        return result
    }
}
