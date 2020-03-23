package com.example.seungwon.openglestriangle.blur.rect

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.seungwon.openglestriangle.R

class BlurScaledActivity : AppCompatActivity(), View.OnTouchListener {
    private var renderRectWidth: Int = 0
    private var renderRectHeight: Int = 0

    private var glSurfaceView: GLSurfaceView? = null
    private val renderer = BlurRendererWithMapper2(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        glSurfaceView = findViewById(R.id.gl_surface)
        glSurfaceView?.let {
            it.setEGLContextClientVersion(2)
            it.setRenderer(renderer)
            // Draw only `requestRender()` called.
            it.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            it.setOnTouchListener(this)
        }

        renderer.onSurfaceSizeChanged = {
            renderRectWidth = it.width()
            renderRectHeight = it.height()
        }
//
//        val saveBtn = findViewById<Button>(R.id.blur_save)
//        saveBtn.setOnClickListener {
//            val filePath = getTempFilePath(externalCacheDir.absolutePath)
//            val bitmap =
//                Bitmap.createBitmap(renderRectWidth, renderRectHeight, Bitmap.Config.ARGB_8888)
//            glSurfaceView?.queueEvent {
//                glSurfaceView?.requestRender()
//                bitmap.renderToBitmap(renderRectWidth, renderRectHeight)
//                bitmap.saveToFile(filePath)
//                bitmap.recycle()
//            }
//            Log.d(TAG, "SAVE $filePath")
//        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

    private fun getDeltaX(renderRectWidth: Int, scaledWidth: Float): Float =
        (renderRectWidth - scaledWidth) / 2f

    private fun getScaledFactorX(renderRectWidth: Int, scaledWidth: Float): Float =
        renderRectWidth / scaledWidth

    var startPointRawX = 0f
    var startPointRawY = 0f
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                // TODO render rect size
//                if (BlurRendererWithMapper.SCALED_WIDTH > renderRectWidth ||
//                    BlurRendererWithMapper.SCALED_HEIGHT > renderRectHeight
//                ) {
//                    // TODO
//                    Log.d(TAG, "onTouch false")
//                    return false
//                }

//                val boundary = Rect(
//                    deltaX.toInt(),
//                    (deltaX + BlurRendererWithMapper.SCALED_WIDTH).toInt(),
//                    deltaY.toInt(),
//                    (deltaY + BlurRendererWithMapper.SCALED_HEIGHT).toInt()
//                )
//                !boundary.contains(
//                    event.x,
//                    event.y
//                )
                val newPositionXForGl = getPositionFromScaledView(
                    event.x,
                    renderRectWidth,
                    BlurRendererWithMapper2.SCALED_WIDTH
                )
                val newPositionYForGl = getPositionFromScaledView(
                    event.y,
                    renderRectHeight,
                    BlurRendererWithMapper2.SCALED_HEIGHT
                )
                Log.d(TAG, "onTouch newPositionXForGl $newPositionXForGl")
//                Log.d(TAG, "onTouch scaleFactorX $scaleFactorX")
//                Log.d(TAG, "onTouch deltaX $deltaX")
                Log.d(TAG, "onTouch raw ${event.rawX} ${event.rawY}")
                Log.d(TAG, "onTouch normal ${event.x} ${event.y}")

//                renderer.textureRectStartPointX = newPositionXForGl
//                renderer.textureRectStartPointY = newPositionYForGl
//
//                renderer.vertexRectStartPointX = newPositionXForGl
//                renderer.vertexRectStartPointY = newPositionYForGl
                renderer.textureRectStartPointX = newPositionXForGl
                renderer.textureRectStartPointY = newPositionYForGl

                renderer.vertexGlRectStartPointX = newPositionXForGl
                renderer.vertexGlRectStartPointY = newPositionYForGl

                renderer.translationXGl = newPositionXForGl
                renderer.translationYGl = newPositionYForGl

                renderer.translationXOriginal = event.x
                renderer.translationYOriginal = event.y

                startPointRawX = event.rawX
                startPointRawY = event.rawY
//                event.offsetLocation()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                glSurfaceView?.queueEvent {
                    val newPositionXForGl = getPositionFromScaledView(
                        event.x,
                        renderRectWidth,
                        BlurRendererWithMapper.SCALED_WIDTH
                    )
                    val newPositionYForGl = getPositionFromScaledView(
                        event.y,
                        renderRectHeight,
                        BlurRendererWithMapper.SCALED_HEIGHT
                    )

//                    Log.d(TAG, "onTouch ${event.x}")
//                    val translatedMatrix = FloatArray(16)
//                    Matrix.setIdentityM(translatedMatrix, 0)
//                    Matrix.translateM(translatedMatrix, 0, -deltaX, -deltaY, 0f)
//
//                    val scaledMatrix = FloatArray(16)
//                    Matrix.setIdentityM(scaledMatrix, 0)
//                    Matrix.scaleM(scaledMatrix, 0, scaleFactorX, scaleFactorY, 0f)
//
//                    val combinedMatrix = FloatArray(16)
//                    Matrix.setIdentityM(combinedMatrix, 0)
//                    Matrix.multiplyMM(combinedMatrix, 0, translatedMatrix, 0, scaledMatrix, 0)
//
//                    val matrix = android.graphics.Matrix()
//                    matrix.setValues(combinedMatrix)
//                    event.transform(matrix)

//                    Log.d(TAG, "onTouch transform newPositionXForGl ${event.x}  ${event.y}")
                    Log.d(TAG, "onTouch newPositionXForGl $newPositionXForGl $newPositionYForGl")
//                    Log.d(TAG, "onTouch scaleFactorX $scaleFactorX")
//                    Log.d(TAG, "onTouch deltaX $deltaX")
                    Log.d(TAG, "onTouch raw End ${event.rawX} ${event.rawY}")
                    Log.d(TAG, "onTouch normal End ${event.x} ${event.y}")

//                    renderer.textureRectEndPointX = newPositionXForGl
//                    renderer.textureRectEndPointY = newPositionYForGl
//
//                    renderer.vertexRectEndPointX = newPositionXForGl
//                    renderer.vertexRectEndPointY = newPositionYForGl

                    renderer.textureRectEndPointX = newPositionXForGl
                    renderer.textureRectEndPointY = newPositionYForGl

                    renderer.vertexGlRectEndPointX = newPositionXForGl
                    renderer.vertexGlRectEndPointY = newPositionYForGl

                    val convertedStartX = getPositionFromScaledView(
                        startPointRawX,
                        renderRectWidth,
                        BlurRendererWithMapper2.SCALED_WIDTH
                    )
                    val convertedEndX = getPositionFromScaledView(
                        event.rawX,
                        renderRectWidth,
                        BlurRendererWithMapper2.SCALED_WIDTH
                    )

                    val convertedStartY = getPositionFromScaledView(
                        startPointRawY,
                        renderRectHeight,
                        BlurRendererWithMapper2.SCALED_HEIGHT
                    )
                    val convertedEndY = getPositionFromScaledView(
                        event.rawY,
                        renderRectHeight,
                        BlurRendererWithMapper2.SCALED_HEIGHT
                    )

                    renderer.lengthOfFboW =
                        kotlin.math.abs(convertedEndX - convertedStartX).toInt()
                    renderer.lengthOfFboH = kotlin.math.abs(convertedEndY - convertedStartY).toInt()
                    renderer.lengthOfOriginalW = event.rawX - startPointRawX
                    renderer.lengthOfOriginalH = event.rawY - startPointRawY
                    Log.d(
                        TAG,
                        "onTouch lengthOfFboW=${renderer.lengthOfFboW} lengthOfFboH=${renderer.lengthOfFboH}"
                    )

                    renderer.onDrawBlurRect { glSurfaceView?.requestRender() }
                }
                return true
            }
        }
        return false
    }

    private fun getPositionFromScaledView(
        position: Float,
        resolution: Int,
        scaledResolution: Float
    ): Float {
        val deltaX = getDeltaX(resolution, scaledResolution)
        val scaleFactorX = getScaledFactorX(resolution, scaledResolution)

        return (position - deltaX) * scaleFactorX
    }

    companion object {
        private const val TAG = "BlurScaledActivity"
    }
}
