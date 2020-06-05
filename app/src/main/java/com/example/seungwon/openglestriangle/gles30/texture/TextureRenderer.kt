package com.example.seungwon.openglestriangle.gles30.texture

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.seungwon.openglestriangle.R
import com.example.seungwon.openglestriangle.gles30.drawer.SquareDrawer
import com.example.seungwon.openglestriangle.gles30.drawer.TextureDrawer
import com.example.seungwon.openglestriangle.util.TxtLoaderUtil
import com.example.seungwon.openglestriangle.util.gles30.ProgramLoader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val textureDrawer: TextureDrawer = TextureDrawer()
    private val squareDrawer: SquareDrawer = SquareDrawer()

    private var programHandle: Int = 0
    private var bitmapHandle: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT)

//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, bitmapHandle)
//        textureDrawer.draw()
        squareDrawer.draw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        if (gl == null) {
            return
        }
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        Log.d("GL", "GL_RENDERER = " + gl.glGetString(GL10.GL_RENDERER))
        Log.d("GL", "GL_VENDOR = " + gl.glGetString(GL10.GL_VENDOR))
        Log.d("GL", "GL_VERSION = " + gl.glGetString(GL10.GL_VERSION))
        Log.i("GL", "GL_EXTENSIONS = " + gl.glGetString(GL10.GL_EXTENSIONS))

        programHandle = ProgramLoader.load(
            context,
            "shaders/gl_es_30/triangle_texture.vert",
            "shaders/gl_es_30/triangle.frag"
        )

        squareDrawer.init(programHandle)
//        textureDrawer.init(programHandle)

        val bitmap = TxtLoaderUtil.getBitmap(context, R.drawable.ic_500_800)
        bitmapHandle = TxtLoaderUtil.getTxt(bitmap)
        bitmap.recycle()
    }

    companion object {
        private const val TAG = "TriangleRenderer"
    }
}
