package com.example.seungwon.openglestriangle

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_VALIDATE_STATUS
import android.opengl.GLES20.glGetProgramInfoLog
import android.opengl.GLES20.glValidateProgram
import android.opengl.GLES30
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
        Log.v(
            "ProgramInfo", "Results of validating program: " + validateStatus[0]
                    + "\nLog:" + glGetProgramInfoLog(programObjectId)
        )

        val result = validateStatus[0] == GLES20.GL_FALSE

        if (result) {
            Log.v("ProgramInfo", "Results of validating Fails")
        }

        Log.v("ProgramInfo", "Results of validating Success")
        return result
    }

    fun createProgramV30(context: Context, vertexShaderId: Int, fragmentShaderId: Int): Int {
        val vertexCodeString =
            TextResourceReader.readTextFileFromResource(context, vertexShaderId)
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES30.glShaderSource(vertexShader, vertexCodeString)
        GLES30.glCompileShader(vertexShader)

        ShaderStatusInfo.getShaderStatus(vertexShader)

        val fragmentCodeString =
            TextResourceReader.readTextFileFromResource(context, fragmentShaderId)
        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES30.glShaderSource(fragmentShader, fragmentCodeString)
        GLES30.glCompileShader(fragmentShader)

        ShaderStatusInfo.getShaderStatus(fragmentShader)

        // Link verticesShader, fragmentShader to OpenGL
        val program = GLES30.glCreateProgram()

        if (program == 0) {
            throw Error("Could not create new program")
        }

        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)

        GLES30.glLinkProgram(program)
        validateProgram(program)
        return program
    }

    fun createProgram(context: Context, vertexShaderId: Int, fragmentShaderId: Int): Int {
        val vertexCodeString =
            TextResourceReader.readTextFileFromResource(context, vertexShaderId)
        val fragmentCodeString =
            TextResourceReader.readTextFileFromResource(context, fragmentShaderId)

        return createProgram(vertexCodeString, fragmentCodeString)
    }

    fun createProgram(vertexShaderStr: String, fragmentShaderStr: String): Int {
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)

        GLES20.glShaderSource(vertexShader, vertexShaderStr)
        GLES20.glCompileShader(vertexShader)

        ShaderStatusInfo.getShaderStatus(vertexShader)
        GLES20.glShaderSource(fragmentShader, fragmentShaderStr)
        GLES20.glCompileShader(fragmentShader)

        ShaderStatusInfo.getShaderStatus(fragmentShader)

        // Link verticesShader, fragmentShader to OpenGL
        val program = GLES20.glCreateProgram()

        if (program == 0) {
            throw Error("Could not create new program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)

        GLES20.glLinkProgram(program)
        validateProgram(program)
        return program
    }
}
