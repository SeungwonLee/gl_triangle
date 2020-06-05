package com.example.seungwon.openglestriangle.util.gles30

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import com.example.seungwon.openglestriangle.ProgramInfo
import com.example.seungwon.openglestriangle.ShaderStatusInfo
import com.example.seungwon.openglestriangle.TextResourceReader

class ProgramLoader {
    companion object {
        private const val TAG = "ProgramLoader"

        fun load(
            context: Context,
            vertexShaderFileName: String,
            fragmentShaderFileName: String
        ): Int {
            val vertexCodeString =
                TextResourceReader.readTextFileFromAsset(
                    context,
                    vertexShaderFileName
                )
            val vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER)
            GLES30.glShaderSource(vertexShader, vertexCodeString)
            GLES30.glCompileShader(vertexShader)

            ShaderStatusInfo.getShaderStatus(vertexShader)

            val fragmentCodeString =
                TextResourceReader.readTextFileFromAsset(
                    context,
                    fragmentShaderFileName
                )
            val fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER)
            GLES30.glShaderSource(fragmentShader, fragmentCodeString)
            GLES30.glCompileShader(fragmentShader)

            ShaderStatusInfo.getShaderStatus(fragmentShader)

            // Link verticesShader, fragmentShader to OpenGL
            val programHandle = GLES30.glCreateProgram()

            if (programHandle == 0) {
                Log.w(TAG, "Could not create new program")
            }

            GLES30.glAttachShader(programHandle, vertexShader)
            GLES30.glAttachShader(programHandle, fragmentShader)

            GLES30.glLinkProgram(programHandle)

            ProgramInfo.validateProgram(programHandle)

            GLES30.glUseProgram(programHandle)

            return programHandle
        }
    }
}