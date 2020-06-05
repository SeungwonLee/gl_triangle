package com.example.seungwon.openglestriangle.gles30.drawer

import android.opengl.GLES30
import com.example.seungwon.openglestriangle.model.Texture

class TextureDrawer {
    private val texture: Texture = Texture()
    private var textureHandle: Int = 0

    fun init(program: Int) {
        textureHandle = GLES30.glGetAttribLocation(
            program,
            TEXTURE_HANDLE_NAME
        )
    }

    fun draw() {
        GLES30.glEnableVertexAttribArray(textureHandle)
        GLES30.glVertexAttribPointer(
            textureHandle,
            Texture.TXT_COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, texture.getTextureBuffer()
        )
    }

    companion object {
        private const val TEXTURE_HANDLE_NAME = "a_texCoord"
    }
}
