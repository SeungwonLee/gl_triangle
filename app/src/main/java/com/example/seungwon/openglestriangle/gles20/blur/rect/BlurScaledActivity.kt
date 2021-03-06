package com.example.seungwon.openglestriangle.gles20.blur.rect

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.seungwon.openglestriangle.R

// TODO
class BlurScaledActivity : AppCompatActivity(), View.OnTouchListener {
    private var renderRectWidth: Int = 0
    private var renderRectHeight: Int = 0

    private var glSurfaceView: GLSurfaceView? = null
    private val renderer = BlurRendererCroppingWithMatrix(this)

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
//            if (it.width() > it.height()) {
//                renderRectWidth = 2240//it.width()
//                renderRectHeight = 1440//it.height()
//
//                SCALED_WIDTH = 2240f//it.width().toFloat()
//                SCALED_HEIGHT = 1440f//it.height().toFloat()
//            } else {
//                renderRectWidth = 1440
//                renderRectHeight = 2240
//
//                SCALED_WIDTH = 1440f
//                SCALED_HEIGHT = 2240f
//            }

            renderRectWidth = it.width()
            renderRectHeight = it.height()

            glSurfaceView?.requestRender()

            Log.d(TAG, "w=$renderRectWidth h=$renderRectHeight")
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

//                Log.d(TAG, "onTouch scaleFactorX $scaleFactorX")
//                Log.d(TAG, "onTouch deltaX $deltaX")
                Log.d(TAG, "onTouch raw ${event.rawX} ${event.rawY}")
                Log.d(TAG, "onTouch normal ${event.x} ${event.y}")

                val newPositionXForView = getPositionFromScaledView(
                    event.x,
                    renderRectWidth,
                    SCALED_WIDTH
                )
                val newPositionYForView = getPositionFromScaledView(
                    event.y,
                    renderRectHeight,
                    SCALED_HEIGHT
                )

                renderer.textureRectStartPointX = newPositionXForView
                renderer.textureRectStartPointY = newPositionYForView

                renderer.vertexGlRectStartPointX = newPositionXForView
                renderer.vertexGlRectStartPointY = newPositionYForView

                val newPositionXForView2 = getPositionFromScaledView(
                    event.x,
                    renderRectWidth,
                    SCALED_WIDTH
                )
                val newPositionYForView2 = getPositionFromScaledView(
                    event.y,
                    renderRectHeight,
                    SCALED_HEIGHT
                )

                renderer.translationXGl = newPositionXForView2//event.rawX
                renderer.translationYGl = newPositionYForView2//event.rawY

                renderer.translationXOriginal = event.x
                renderer.translationYOriginal = event.y

                Log.d(TAG, "onTouch down xy=${event.x}/${event.y} rxy=${event.rawX}/${event.rawY}")

                startPointRawX = event.rawX
                startPointRawY = event.rawY
//                event.offsetLocation()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "onTouch move xy=${event.x}/${event.y} rxy=${event.rawX}/${event.rawY}")
                return true
            }
            MotionEvent.ACTION_UP -> {
                glSurfaceView?.queueEvent {
                    val newPositionXForView = getPositionFromScaledView(
                        event.x,
                        renderRectWidth,
                        SCALED_WIDTH
                    )
                    val newPositionYForView = getPositionFromScaledView(
                        event.y,
                        renderRectHeight,
                        SCALED_HEIGHT
                    )

                    Log.d(
                        TAG,
                        "r.w $renderRectWidth, r.h $renderRectHeight " +
                                "s.w $SCALED_WIDTH s.h $SCALED_HEIGHT"
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
                    Log.d(
                        TAG,
                        "onTouch newPositionXForGl $newPositionXForView $newPositionYForView"
                    )
//                    Log.d(TAG, "onTouch scaleFactorX $scaleFactorX")
//                    Log.d(TAG, "onTouch deltaX $deltaX")
                    Log.d(
                        TAG,
                        "onTouch move xy=${event.x}/${event.y} rxy=${event.rawX}/${event.rawY}"
                    )

                    renderer.textureRectEndPointX = newPositionXForView
                    renderer.textureRectEndPointY = newPositionYForView

                    renderer.vertexGlRectEndPointX = newPositionXForView
                    renderer.vertexGlRectEndPointY = newPositionYForView

                    val convertedStartX = getPositionFromScaledView(
                        startPointRawX,
                        renderRectWidth,
                        SCALED_WIDTH
                    )
                    val convertedStartY = getPositionFromScaledView(
                        startPointRawY,
                        renderRectHeight,
                        SCALED_HEIGHT
                    )

                    val convertedEndX = getPositionFromScaledView(
                        event.rawX,
                        renderRectWidth,
                        SCALED_WIDTH
                    )
                    val convertedEndY = getPositionFromScaledView(
                        event.rawY,
                        renderRectHeight,
                        SCALED_HEIGHT
                    )

                    renderer.scaledOriginalRectW = (convertedEndX - convertedStartX).toInt()
                    //kotlin.math.abs(event.rawX - startPointRawX).toInt()
                    renderer.scaledOriginalRectH = (convertedEndY - convertedStartY).toInt()
                    //kotlin.math.abs(event.rawY - startPointRawY).toInt()
                    renderer.sizeOfViewRectW = event.rawX - startPointRawX
                    renderer.sizeOfViewRectH = event.rawY - startPointRawY
                    Log.d(
                        TAG,
                        "onTouch lengthOfFboW=${renderer.scaledOriginalRectW} lengthOfFboH=${renderer.scaledOriginalRectH}"
                    )

                    renderer.onDrawBlurRect { glSurfaceView?.requestRender() }
                }
                return true
            }
        }
        return false
    }

    private fun getDeltaX(renderRectWidth: Int, scaledWidth: Float): Float =
        (renderRectWidth - scaledWidth) * 0.5f

    private fun getScaledFactorX(renderRectWidth: Int, scaledWidth: Float): Float =
        renderRectWidth / scaledWidth

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
        const val SCALED_WIDTH = 1440f
        const val SCALED_HEIGHT = 2240f
    }
}
